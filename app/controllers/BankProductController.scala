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
(bankService: BankService, cc: MessagesControllerComponents, authenticatedUserAction: AuthenticatedUserAction
 ) extends MessagesAbstractController(cc) with I18nSupport {

/**
 * Home Page.
 *
 * @return The result to display.
 */
  
  val bankProductForm: Form[BankProductForm] = Form(
  mapping(
    "name"        -> nonEmptyText(maxLength = 120),
    "owner"        -> nonEmptyText(maxLength = 120),
    "bankId"      -> number
)(BankProductForm.apply)(BankProductForm.unapply))

val editForm: Form[BankProduct] = Form(
  mapping(
    "name"        -> nonEmptyText(maxLength = 120),
    "owner"        -> nonEmptyText(maxLength = 120),
    "bankId"      -> number,
    "id"          -> number
)(BankProduct.apply)(BankProduct.unapply))
   


private val logoutUrl = routes.AuthenticatedUserController.logout

var own: String = " "


def listBankAndProducts() = Action.async { implicit  request =>
    val maybeUsername = request.session.get(model.Global.SESSION_USERNAME_KEY)
      maybeUsername match {
            case None => {
                Future.successful(BadRequest("You’re not logged in."))
            }
            case Some(u) => {
                own = u
                val bankProduct = bankService.getBankWithProduct()
                 bankProduct map { banks =>
                  Ok(views.html.showNotes(u, banks))
              }
              
            }    
    }
  
  
    
  }
  
  def listAll() = Action.async { implicit  request => 
    bankService.getAllBankWithProduct() map { banks =>
     
      Ok(views.html.allNotes(bankProductForm, banks))
    }
  }


  def listBankProducts() = Action.async { implicit  request =>
    val maybeUsername = request.session.get(model.Global.SESSION_USERNAME_KEY)
      maybeUsername match {
            case None => {
                Future.successful(BadRequest("You’re not logged in."))
            }
            case Some(u) => {
                own = u
                bankService.getBankAll() map { banks =>
                Ok(views.html.note(u, bankProductForm, banks))
              }
            }    
    }
    
  }
  
  def addBankProduct() = Action.async { implicit request =>    
     var list = List.empty[dao.Bank]
     bankProductForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(views.html.note(own, errorForm,  list))),
      data => { 
        val newBank = BankProduct(data.name, data.owner, data.bankId, 1)
        bankService.create(newBank)
        Future.successful(Redirect(routes.BankProductController.listBankAndProducts)
                    .flashing("Success" -> "Note added")) 
              
      })
  }
  
  def confirmEdit() = Action.async { implicit request =>    
    
     editForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Redirect(routes.BankProductController.listBankAndProducts())),
      data => { 
        Console.println(data.name, data.bankId, data.id)
        bankService.update(BankProduct(data.name, data.owner, data.bankId, data.id))
        Future.successful(Redirect(routes.BankProductController.listBankAndProducts())
                    .flashing("Success" -> "Note added"))               
      })
  }
  
/**
 * Delete User.
 *
 * @return The result to display.
 */
  def deleteBankProduct(id : Integer) = Action.async { implicit request =>
    bankService.delete(id) map { res =>
      Redirect(routes.BankProductController.listBankAndProducts())
    }
  }
  
  
  def editData(id : Integer) = Action.async { implicit request =>
    val eBank = bankService.getById(id)
    eBank.flatMap {
          case Some(a) => val filledForm = editForm.fill(BankProduct(a.name, a.owner,  a.bankId, a.id))
          bankService.getBankAll() map { banks =>
            
            Ok(views.html.editPage(filledForm))}
            
          case None =>  Future.successful(Redirect(routes.BankController.listBanks)
                    .flashing("Success" -> "Bank added")) 
     }   
    
  }
  
}