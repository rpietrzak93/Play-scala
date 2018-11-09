package dao


import com.google.inject.ImplementedBy
import model.User
import model.Login
import scala.concurrent.Future

@ImplementedBy(classOf[UserDAOImpl])
trait UserDAO {

  def add(user:User) : Future[String]
  def get(id : Long) : Future[Option[User]]
  def delete(id : Long) : Future[Int]
  def listAll : Future[Seq[User]]
  def editUser(user:User) : Future[String]
  def findByLogin(login: String) : Future[Option[User]]
  def lookupUser(log: Login) : Boolean

}

import javax.inject.Inject
import com.google.inject.Singleton
import model.User
import model.Login
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

@Singleton
class UserDAOImpl @Inject()
(dbConfigProvider: DatabaseConfigProvider) extends UserDAO {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UserTable(tag:Tag)
    extends Table[User](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
    def login = column[String]("login")
    def password = column[String]("password")
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def mobile = column[Long]("mobile")
    def email = column[String]("email")

    override def * =
      (id, login, password, firstName,lastName,mobile,email) <> (User.tupled, User.unapply)
  }

implicit val users = TableQuery[UserTable]
  


  override def add(user: User): Future[String] = {
    db.run(users += user).map(res => "User successfully added")
  }

  override def delete(id: Long): Future[Int] = {
    db.run(users.filter(_.id === id).delete)
  }

  override def get(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }
  
  override def listAll: Future[Seq[User]] = {
    db.run(users.result)
  }
  
  def findByLogin(login: String) : Future[Option[User]] =
  {
    db.run(users.filter(_.login === login).result.headOption)
  }
  

  def editUser(user: User): Future[String] = {
    if (user.id == 0) {
      add(user)
    } else {
      db.run(users
        .filter(_.id === user.id)
        .update(user))
        .map(res => s"Category user.id successfully updated")
        .recover {
          case ex: Exception => ex.getCause.getMessage
        }
    }
  }

  def lookupUser(log: Login): Boolean = {
        //TODO query your database here
    
      if (log.username == "foo" && log.password == "foo") true else false
   }


  
  
  
  
  
}

