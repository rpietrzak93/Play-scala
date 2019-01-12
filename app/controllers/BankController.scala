package controllers

import service.BankService

import com.google.inject.Inject

import play.api.mvc.{Action, Controller}
import service.UserService
import scala.concurrent.Future
import play.api.i18n.{MessagesApi, Messages, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits._
import dao.BankProduct
import dao.Bank
import dao.BankForm

import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import scala.util.Success

class BankController @Inject()
(bankService: BankService, cc: MessagesControllerComponents, authenticatedUserAction: AuthenticatedUserAction
 ) extends MessagesAbstractController(cc) with I18nSupport {

/**
 * Home Page.
 *
 * @return The result to display.
 */
  
  val bankForm: Form[BankForm] = Form(
  mapping(
      "name"        -> nonEmptyText(maxLength = 140),
      "owner"        -> nonEmptyText(maxLength = 140),
   
)(BankForm.apply)(BankForm.unapply))
   

  var own: String = " "


  def listBanks() = Action.async { implicit  request =>
    val maybeUsername = request.session.get(model.Global.SESSION_USERNAME_KEY)
      maybeUsername match {
            case None => {
                Future.successful(BadRequest("You’re not logged in."))
            }
            case Some(u) => {
                own = u
                bankService.getBankAll() map { banks =>
                Ok(views.html.topic(u, bankForm,  banks))
              }
            }    
    }
  }
  
  def addBank() = Action.async { implicit request =>     
     bankForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(views.html.topic(own, errorForm, Seq.empty[Bank]))),
      data => { 
        val newBank = Bank(data.name, data.owner, 0)
        bankService.createBank(newBank)
        Future.successful(Redirect(routes.BankController.listBanks)
                    .flashing("Success" -> "Bank added")) 
              
      })
  }
  
/**
 * Delete User.
 *
 * @return The result to display.
 */
  def deleteBank(id : Integer) = Action.async { implicit request =>
    
    bankService.deleteBank(id) map { res =>
      Redirect(routes.BankController.listBanks)
    }
  }
  
}