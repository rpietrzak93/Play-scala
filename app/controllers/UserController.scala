package controllers

import com.google.inject.Inject
import model.{User, Global, UserFormData, UserForm}

import play.api.mvc.{Action, Controller}
import service.UserService
import scala.concurrent.Future
import play.api.i18n.{MessagesApi, Messages, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits._


import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import scala.util.Success

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

 

class UserController @Inject()
(userService: UserService, cc: MessagesControllerComponents,
 ) extends MessagesAbstractController(cc) with I18nSupport {

/**
 * Home Page.
 *
 * @return The result to display.
 */
   
  def home() = Action.async { implicit  request =>
    userService.listAllUsers map { users =>
      Ok(views.html.user(UserForm.userForm, users))
    }
  }

/**
 * Add User.
 *
 * @return The result to display.
 * 
 */
  
  def addUser() = Action.async { implicit request =>     
     UserForm.userForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(views.html.user(errorForm, Seq.empty[User]))),
      data => { 
        
        val existUser = userService.findByLogin(data.login) 
        val newUser = User(0,data.login, data.password, data.firstName,data.lastName,data.mobile,data.email)
        
          existUser.flatMap {
          case Some(a) => val form =  UserForm.userForm.fill(data).withError("userExists", "User with this login already exists!")
                Future.successful(BadRequest(views.html.user(form, Seq.empty[User])))
          case None => userService.addUser(newUser).map(res =>
              Redirect(routes.LoginController.showLoginForm).flashing("Success" -> "Registration success")) 
        }       
      })
  }
  
/**
 * Delete User.
 *
 * @return The result to display.
 */
  def deleteUser(id : Long) = Action.async { implicit request =>
    userService.deleteUser(id) map { res =>
      Redirect(routes.UserController.home())
    }
  }
  
  def editUser(id : Long) = Action {
    Redirect(routes.UserController.home())
  }  


}