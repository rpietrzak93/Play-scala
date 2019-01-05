package controllers

import service.BankService

import com.google.inject.Inject

import play.api.mvc.{Action, Controller}
import service.UserService
import scala.concurrent.Future
import play.api.i18n.{MessagesApi, Messages, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits._
import dao.BankProduct
import dao.BankProductForm
import dao.Bank
import dao.BankForm

import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import scala.util.Success

class BankProductController @Inject()
(bankService: BankService, cc: MessagesControllerComponents,
 ) extends MessagesAbstractController(cc) with I18nSupport {

/**
 * Home Page.
 *
 * @return The result to display.
 */
  
  val bankProductForm: Form[BankProductForm] = Form(
  mapping(
    "name"        -> nonEmptyText(maxLength = 14),
    "bankId"      -> number
)(BankProductForm.apply)(BankProductForm.unapply))
   
  def listBankProducts() = Action.async { implicit  request =>
    //bankService.create("dollar", 2, Some(1))
    //bankService.create(BankProduct("xxx", 5, Some(1)))
    
    bankService.getBankAll() map { banks =>
      Ok(views.html.bankProduct(bankProductForm, banks))
    }
  }
  
  def addBankProduct() = Action.async { implicit request =>    
     var list = List.empty[dao.Bank]
     bankProductForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(views.html.bankProduct(errorForm,  list))),
      data => { 
        val newBank = BankProduct(data.name, data.bankId, 1)
        bankService.create(newBank)
        Future.successful(Redirect(routes.BankProductController.listBankProducts)
                    .flashing("Success" -> "Bank added")) 
              
      })
  }
  
/**
 * Delete User.
 *
 * @return The result to display.
 */
  def deleteBankProduct(id : Integer) = Action.async { implicit request =>
    bankService.delete(id) map { res =>
      Redirect(routes.BankProductController.listBankProducts())
    }
  }
  
}