package dao

import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[BankDAOImpl])
trait BankDAO {

  def createBank(bank: Bank): Future[Int]
  def updateBank(bank: Bank): Future[Int]
  def getBankById(id: Int): Future[Option[Bank]]
  def getBankAll(): Future[List[Bank]]
  def deleteBank(id: Int): Future[Int]
  
  
  def create(bankProduct: BankProduct): Future[Int]
  def update(bankProduct: BankProduct): Future[Int]
  def getById(id: Int): Future[Option[BankProduct]]
  def getAll(): Future[List[BankProduct]]
  def delete(id: Int): Future[Int]
  def getBankWithProduct(): Future[List[(Bank, BankProduct)]]
  def getAllBankWithProduct(): Future[List[(Bank, Option[BankProduct])]]
  
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
class BankDAOImpl @Inject()
(dbConfigProvider: DatabaseConfigProvider) extends BankDAO {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class BankTable(tag: Tag) extends Table[Bank](tag,"bank") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")    
    
    def owner = column[String]("owner")
    
    override def * = (name, owner, id) <> (Bank.tupled, Bank.unapply)
    //override def * = (name, id.?) <> (Bank.tupled, Bank.unapply)
  }
  
  class BankProductTable(tag: Tag) extends Table[BankProduct](tag, "bankproduct") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def owner = column[String]("owner")
    def bankId = column[Int]("bank_id")
    def bank = foreignKey("bankproduct_bank_id_fkey", bankId, bankTableQuery)(_.id)
    override def * = (name, owner, bankId, id) <> (BankProduct.tupled, BankProduct.unapply)
    //override def * = (name, bankId, id.?) <> (BankProduct.tupled, BankProduct.unapply)

  }

  implicit val bankProductTableQuery = TableQuery[BankProductTable]

  protected def bankProductTableAutoInc = bankProductTableQuery returning bankProductTableQuery.map(_.id)

  implicit val bankTableQuery = TableQuery[BankTable]

  protected def bankTableAutoInc = bankTableQuery returning bankTableQuery.map(_.id)

  def createBank(bank: Bank): Future[Int] = {
    db.run { bankTableAutoInc += bank }
  }

  def updateBank(bank: Bank): Future[Int] = {
    if (bank.id == 0) {
      createBank(bank)
    } else {
      db.run(bankTableQuery
        .filter(_.id === bank.id)
        .update(bank))
        .map(res => s"Category user.id successfully updated")
        .recover {
          case ex: Exception => ex.getCause.getMessage
        }
    }
    db.run { bankTableQuery.filter(_.id === bank.id).update(bank) }
    //db.run { bankTableQuery.filter(_.id === bank.id.get).update(bank) }
  }

  def getBankById(id: Int): Future[Option[Bank]] = {
    db.run { bankTableQuery.filter(_.id === id).result.headOption }
  }

  def getBankAll(): Future[List[Bank]] = {
    db.run { bankTableQuery.to[List].result }
  }

  def deleteBank(id: Int): Future[Int] = {
    db.run { bankTableQuery.filter(_.id === id).delete }
  }
  
  
  
   def create(bankProduct: BankProduct): Future[Int] = {
    db.run { bankProductTableAutoInc += bankProduct }
  }

  def update(bankProduct: BankProduct): Future[Int] = {
    db.run { bankProductTableQuery.filter(_.id === bankProduct.id).update(bankProduct) }
    //db.run { bankProductTableQuery.filter(_.id === bankProduct.id.get).update(bankProduct) }
  }

  def getById(id: Int): Future[Option[BankProduct]] = {
    db.run { bankProductTableQuery.filter(_.id === id).result.headOption }
  }

  def getAll(): Future[List[BankProduct]] = {
    db.run { bankProductTableQuery.to[List].result }
  }

  def delete(id: Int): Future[Int] = {
    db.run { bankProductTableQuery.filter(_.id === id).delete }
  }

  /**
   * Get bank and product using foreign key relationship
   */
  def getBankWithProduct(): Future[List[(Bank, BankProduct)]] =
    db.run {
      (for {
        product <- bankProductTableQuery
        bank <- product.bank
      } yield (bank, product)).to[List].result
    }

  /**
   * Get all bank and their product.It is possible some bank do not have their product
   */
  def getAllBankWithProduct(): Future[List[(Bank, Option[BankProduct])]] =
    db.run {
      bankTableQuery.joinLeft(bankProductTableQuery).on(_.id === _.bankId).to[List].result
    }

}

case class Bank(name: String, owner:String, id: Int)
case class BankForm(name: String, owner: String)

case class BankProduct(name: String, owner: String, bankId: Int, id: Int) //Option[Int] = None
case class BankProductForm(name: String, owner: String, bankId: Int) 




