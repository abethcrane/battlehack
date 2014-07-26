from server import app, db
import models

from flask.ext import admin
from flask.ext.admin.contrib import sqla

class VendorAdmin(sqla.ModelView):
  def __init__(self, session):
    super(VendorAdmin, self).__init__(models.Vendor, session)

admin = admin.Admin(app)

admin.add_view(VendorAdmin(db.session))
