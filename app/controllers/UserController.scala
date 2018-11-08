package controllers

import com.google.inject.Inject
import model.{User, UserForm, Global, UserFormData}

import play.api.mvc.{Action, Controller}
import service.UserService
import scala.concurrent.Future
import play.api.i18n.{MessagesApi, Messages, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits._


import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

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
 
      errorForm => Future.successful(Ok(views.html.user(errorForm, Seq.empty[User]))),
      data => {
        val newUser = User(0,data.login, data.password, data.firstName,data.lastName,data.mobile,data.email)
   
        userService.addUser(newUser).map(res =>
          Redirect(routes.HomeController.index()).flashing("success" -> "Registration success"))
        
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
  
  def editUser(id : Long) = Action.async { implicit request =>
    userService.editUser(id) map { res =>
      Redirect(routes.UserController.home())
    }
  }  


}