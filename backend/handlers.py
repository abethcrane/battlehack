import braintree
from flask import render_template, request
from flask.json import jsonify

from server import app, db
import models

@app.route('/')
def root():
  return render_template('root.html')


@app.route('/client/get_token/<customer_id>')
def get_token(customer_id):
  client_token = braintree.ClientToken.generate()
  return jsonify({'status': 'ok', 'token': client_token})


@app.route('/client/finish', methods=['POST'])
def complete_payment():
  result = braintree.Transaction.sale({
      "amount" : request.form["amount"],
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
