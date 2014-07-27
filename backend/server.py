import braintree
from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext.login import LoginManager

import config

# Set this up before touching handlers, since app is used in decorators.
# Simiarly db for models.
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = config.database
db = SQLAlchemy(app)
login_manager = LoginManager()
login_manager.init_app(app)

# Now we can import the rest
from admin import *
from handlers import *
from models import *

login_manager.user_loader(User.get_by_username)
login_manager.unauthorized = lambda: redirect('/login')

if __name__ == '__main__':
  db.create_all()

  o1 = Organisation(name='The Big Issue')
  o2 = Organisation(name='Helping Hands')
  u = User(username='evgeny', organisation=o1)
  u.set_password('potato')

  for obj in [u, o2]:
    db.session.add(obj)
    try:
      db.session.commit()
    except Exception as e:  # IntegrityError
      print e
      db.session.rollback()

  braintree.Configuration.configure(braintree.Environment.Sandbox,
      merchant_id=config.merchant_id,
      public_key=config.public_key,
      private_key=config.private_key)

  app.debug = config.debug
  app.secret_key = config.secret_key
  app.run(host='0.0.0.0')
