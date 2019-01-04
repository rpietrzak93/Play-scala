package controllers

import javax.inject.Inject
import model.{Global, Login}
import dao.UserDAO
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginController @Inject()(
    cc: MessagesControllerComponents,
    userDao: UserDAO
) extends MessagesAbstractController(cc) {

    private val logger = play.api.Logger(this.getClass)

    val form: Form[Login] = Form (
        mapping(
            "username" -> nonEmptyText
                .verifying("too few chars",  s => lengthIsGreaterThanNCharacters(s, 2))
                .verifying("too many chars", s => lengthIsLessThanNCharacters(s, 20)),
            "password" -> nonEmptyText
                .verifying("too few chars",  s => lengthIsGreaterThanNCharacters(s, 2))
                .verifying("too many chars", s => lengthIsLessThanNCharacters(s, 30)),
        )(Login.apply)(Login.unapply)
    )

    private val formSubmitUrl = routes.LoginController.processLoginAttempt

    def showLoginForm = Action { implicit request: MessagesRequest[AnyContent] =>
        Ok(views.html.userLogin(form, formSubmitUrl))
    }
    
    def processLoginAttempt = Action.async { implicit request =>     
      val formValidationResult: Form[Login] = form.bindFromRequest
        formValidationResult.fold(
        formWithErrors => Future.successful(BadRequest(views.html.userLogin(formWithErrors, formSubmitUrl))),
        data => { 
          val existUser = userDao.findByLogin(data.username)         
          existUser.flatMap {
          case Some(valu) => {
            if(valu.password == data.password){
              Future.successful(Redirect(routes.LandingPageController.showLandingPage)
                    .flashing("info" -> "You are logged in.")
                    .withSession(Global.SESSION_USERNAME_KEY -> data.username))
            }
            else{
              Future.successful(Redirect(routes.LoginController.showLoginForm)
                    .flashing("error" -> "Invalid username/password."))  
            }
              }                                
         case None => Future.successful(Redirect(routes.LoginController.showLoginForm)
                    .flashing("error" -> "Invalid username/password."))   
        }       
      })
     
  }


    private def lengthIsGreaterThanNCharacters(s: String, n: Int): Boolean = {
        if (s.length > n) true else false
    }

    private def lengthIsLessThanNCharacters(s: String, n: Int): Boolean = {
        if (s.length < n) true else false
    }
}