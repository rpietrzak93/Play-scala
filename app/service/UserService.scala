package service

import com.google.inject.ImplementedBy
import model.User
import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {
  def addUser(user:User) : Future[String]
  def getUser(id : Long) : Future[Option[User]]
  def deleteUser(id : Long) : Future[Int]
  def listAllUsers : Future[Seq[User]]
  def editUser(id: Long) : Future[Int]
  def findByLogin(login: String) : Future[Option[User]]
}

import javax.inject.Inject
import com.google.inject.Singleton
import dao.UserDAO
import model.User
import scala.concurrent.Future

@Singleton
class UserServiceImpl @Inject()(userDAO: UserDAO) extends UserService {
  
  override def addUser(user: User): Future[String] = {
    userDAO.add(user)
  }

  override def deleteUser(id: Long): Future[Int] = {
    userDAO.delete(id)
  }

  override def getUser(id: Long): Future[Option[User]] = {
    userDAO.get(id)
  }

  override def listAllUsers: Future[Seq[User]] = {
    userDAO.listAll
  }
  
  def editUser(id: Long): Future[Int] = {
    userDAO.editUser(id)    
  }
  def findByLogin(login: String) : Future[Option[User]] = {
    userDAO.findByLogin(login)
  }
  
}