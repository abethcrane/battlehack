from server import app, db
import models

from flask import redirect, url_for
from flask.ext import admin, login
from flask.ext.admin import expose
from flask.ext.admin.contrib import sqla


class VendorAdmin(sqla.ModelView):
  column_labels = dict(bluetooth='Bluetooth ID', vendor='Name')
  column_searchable_list = (models.Vendor.bluetooth, models.Vendor.vendor)

  def __init__(self, session):
    super(VendorAdmin, self).__init__(models.Vendor, session)

  def is_accessible(self):
    return login.current_user.is_authenticated()

  def get_query(self):
    q = super(VendorAdmin, self).get_query()
    q = q.filter(models.Vendor.organisation==login.current_user)
    return q


class AdminIndexView(admin.AdminIndexView):
  @expose('/')
  def index(self):
    if not login.current_user.is_authenticated():
      return redirect('/login')
    return super(AdminIndexView, self).index()


admin = admin.Admin(app, index_view=AdminIndexView())
admin.add_view(VendorAdmin(db.session))
