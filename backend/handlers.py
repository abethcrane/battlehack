import braintree
from flask import render_template, redirect, request, url_for
from flask.json import jsonify
from flask.ext import login

import config
from server import app, db, login_manager
import models

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
      return redirect(url_for('root'))
    else:
      error = True

  return render_template('login.html', error=error)


@app.route('/logout')
def auth_logout():
  login.logout_user()
  return redirect(url_for('root'))

@app.route('/client/get_token/<customer_id>')
def get_token(customer_id):
  client_token = braintree.ClientToken.generate()
  return jsonify({'status': 'ok', 'token': client_token})


@app.route('/client/finish', methods=['POST'])
def complete_payment():
  result = braintree.Transaction.sale({
      "amount" : config.purchase_price,
      "payment_method_nonce" : request.form["payment_method_nonce"]
  })

  if result.is_success:
      result = {'status': 'ok', 'transaction_id': result.transaction.id}
  else:
      result = {'status': 'error', 'message': result.message}

  return jsonify(result)


@app.route('/vendors/find')
def find_vendors():
  ids = request.args.getlist('ids')
  vendors = models.Vendor.filter_by_ids(ids)
  vendors = [{'id': v.bluetooth, 'vendor': v.vendor} for v in vendors]
  return jsonify(vendors)
