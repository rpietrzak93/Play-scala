package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent.Future
import service.UserService
import scala.concurrent.ExecutionContext.Implicits.global
import model.User
import play.api.i18n.{MessagesApi, Messages, I18nSupport}

@Singleton
class LandingPageController @Inject()(
    cc: ControllerComponents, userService: UserService,
    authenticatedUserAction: AuthenticatedUserAction
) extends AbstractController(cc) with I18nSupport{

    private val logoutUrl = routes.AuthenticatedUserController.logout
    var own: String = " "
    
    // this is where the user comes immediately after logging in.
    // notice that this uses `authenticatedUserAction`.
    def showLandingPage() = authenticatedUserAction { implicit request: Request[AnyContent] =>
      
      val maybeUsername = request.session.get(model.Global.SESSION_USERNAME_KEY)
      maybeUsername match {
            case None => {
                Ok("You’re not logged in.")
            }
            case Some(u) => {
                own = u;
                Ok(views.html.loginLandingPage(logoutUrl, u))
            }
        }
    }
    
    def changePassword() = Action.async { implicit request: Request[AnyContent] =>
      val maybeUsername = request.session.get(model.Global.SESSION_USERNAME_KEY)
      maybeUsername match {
            case None => {
                Future.successful(BadRequest("You’re not logged in."))
            }
            case Some(u) => {
                own = u;
                val user = userService.findByLogin(own) 
                user.flatMap {
                 case Some(a) => val filledForm = model.UserForm.editForm.fill(User(a.id, a.login, a.password, a.firstName, a.lastName, a.mobile, a.email))
            
                    Future.successful(Ok(views.html.editUser(filledForm)))
           
            
              case None =>  Future.successful(Redirect(routes.BankController.listBanks)
                    .flashing("Success" -> "No user")) 
         }
            }
        }   
      
        
        
    }
    
    def confirmEditUser() = Action.async { implicit request =>    
    
     model.UserForm.editForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Redirect(routes.BankProductController.listBankAndProducts)),
      data => { 
        Console.println(data)
        userService.editUser(User(data.id, data.login, data.password, data.firstName, data.lastName, data.mobile, data.email))
 
        Future.successful(Redirect(routes.LoginController.showLoginForm)
            .flashing("info" -> "Data was changed. Please login again")
            .withNewSession)         
      })
  }
   

}