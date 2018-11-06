package model
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

case class User(id : Long ,login: String, password: String, firstName : String, lastName : String , mobile : Long , email : String)

case class UserFormData(login: String, password: String, firstName : String, lastName : String , mobile : Long , email : String )

case class UserFormLogin(login: String, password: String)

object UserForm {

  val userForm = Form(
    mapping(
      "login" -> nonEmptyText,
      "password" -> nonEmptyText
       .verifying("Password must have min 5 chars and at least one special char (@#$%^&+=) and one uppercase letter",  s => validatePassword(s, 5)),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobile" -> longNumber,
      "email" -> email,
    )(UserFormData.apply)(UserFormData.unapply))
    
    val login = Form(
     mapping(
       "login" -> nonEmptyText,
       "password" -> nonEmptyText
     )(UserFormLogin.apply)(UserFormLogin.unapply)
   )

    val atLeastOneUpperLetterAndAtLeasOneSpecialChar = """(?=.*[A-Z])(?=.*[@#$%^&+=]).*"""
    
    private def validatePassword(s: String, n: Int): Boolean = {
        if (s.length > n && s.matches(atLeastOneUpperLetterAndAtLeasOneSpecialChar)) true else false
    }

    
}