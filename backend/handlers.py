import braintree
from flask import render_template, redirect, request, url_for
from flask.json import jsonify
from flask.ext import login

import config
from server import app, db, login_manager
import models


def authorise_payment(payment_method_nonce, amount):
  result = braintree.Transaction.sale({
      "amount" : amount,
      "payment_method_nonce" : payment_method_nonce,
  })

  if result.is_success:
      result = {'status': 'ok', 'transaction_id': result.transaction.id}
  else:
      result = {'status': 'error', 'message': result.message}

  print request.form["payment_method_nonce"], result
  return result


def submit_for_settlement(transaction_id, vendor):
  result = braintree.Transaction.submit_for_settlement(transaction_id)
  if result.is_success:
    result = {'status': 'ok', 'keyword': vendor.keyword}
  else:
    result = {'status': 'error', 'message': repr(result.errors)}
  return result


@app.route('/')
def root():
  return render_template('root.html')


@app.route('/login', methods=['GET', 'POST'])
def auth_login():
  error = False

  if request.method == 'POST':
    u = models.User.authenticate_or_none(request.form['username'], request.form['password'])
    if u:
      login.login_user(u)
      return redirect('/admin')
    else:
      error = True

  return render_template('login.html', error=error)


@app.route('/logout')
def auth_logout():
  login.logout_user()
  return redirect(url_for('root'))


#
# OLD API
#
@app.route('/client/get_token/<customer_id>')
def get_token(customer_id):
  client_token = braintree.ClientToken.generate()
  return jsonify({'status': 'ok', 'token': client_token})


@app.route('/client/finish', methods=['POST'])
def complete_payment():
  result = authorise_payment(request.form['payment_method_nonce'], config.purchase_price)
  return jsonify(result)


@app.route('/vendors/redeem', methods=['POST'])
def redeem_token():
  vid = request.form['vendor_id']
  vendor = models.Vendor.get_by_id(vid)
  if not vendor:
    return jsonify({'status': 'error', 'message': 'no such vendor'})

  result = submit_for_settlement(request.form['transaction_id'], vendor)
  return jsonify(result)


#
# NEW API
#
@app.route('/client/instant', methods=['POST'])
def client_instant():
  vid = request.form['vendor_id']
  vendor = models.Vendor.get_by_id(vid)
  if not vendor:
    return jsonify({'status': 'error', 'message': 'no such vendor'})

  result = authorise_payment(request.form['payment_method_nonce'], vendor.price)
  if result['status'] != 'ok':
    return jsonify(result)

  result = submit_for_settlement(result['transaction_id'], vendor)
  return jsonify(result)


@app.route('/vendors/find')
def find_vendors():
  ids = request.args.getlist('ids')
  vendors = models.Vendor.filter_by_ids(ids)
  vendors = [{
      'id': v.id,
      'bluetooth': v.bluetooth,
      'vendor': v.vendor,
      'price': v.price,
      'organisation': v.organisation.name,
      'item_name': v.item_name,
      'url': v.image,
  } for v in vendors]
  return jsonify({'status': 'ok', 'vendors': vendors})
