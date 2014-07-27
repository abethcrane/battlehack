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
  Organisation.query.delete()
  User.query.delete()
  db.session.commit()

  o1 = Organisation(name='The Big Issue')
  o2 = Organisation(name='Helping Hands')
  u1 = User(username='evgeny', organisation=o1)
  u1.set_password('potato')
  u2 = User(username='beth', organisation=o2)
  u2.set_password('helloworld')

  for obj in [o1, o2, u1, u2]:
    db.session.add(obj)
  db.session.commit()

  braintree.Configuration.configure(braintree.Environment.Sandbox,
      merchant_id=config.merchant_id,
      public_key=config.public_key,
      private_key=config.private_key)

  app.debug = config.debug
  app.secret_key = config.secret_key
  app.run(host='0.0.0.0')
