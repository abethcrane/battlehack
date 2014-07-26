import braintree
from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy

import config

# Set this up before touching handlers, since app is used in decorators.
# Simiarly db for models.
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = config.database
db = SQLAlchemy(app)

# Now we can import the rest
from admin import *
from handlers import *
from models import *


if __name__ == '__main__':
  db.create_all()

  braintree.Configuration.configure(braintree.Environment.Sandbox,
      merchant_id=config.merchant_id,
      public_key=config.public_key,
      private_key=config.private_key)

  app.debug = config.debug
  app.run(host='0.0.0.0')
