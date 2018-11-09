package model
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import service.UserService
import javax.inject.Inject


case class User(id : Long ,login: String, password: String, firstName : String, lastName : String , mobile : Long , email : String)

case class UserFormData(login: String, password: String, firstName : String, lastName : String , mobile : Long , email : String )

object UserForm{

  val userForm = Form(
    mapping(
      "login" -> nonEmptyText,
      "password" -> nonEmptyText
       .verifying("Password must have min 5 chars, one special char and one uppercase letter",  s => validatePassword(s, 5)),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobile" -> longNumber,
      "email" -> email,
    )(UserFormData.apply)(UserFormData.unapply))
    

    val atLeastOneUpperLetterAndAtLeasOneSpecialChar = """(?=.*[A-Z])(?=.*[@#$%^&+=]).*"""
    
    private def validatePassword(s: String, n: Int): Boolean = {
        if (s.length > n && s.matches(atLeastOneUpperLetterAndAtLeasOneSpecialChar)) true else false
    }  
}