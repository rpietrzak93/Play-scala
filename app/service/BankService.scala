package service

import com.google.inject.ImplementedBy
import scala.concurrent.Future
import dao.Bank
import dao.BankProduct

@ImplementedBy(classOf[BankServiceImpl])
trait BankService {

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
import dao.BankDAO
import dao.BankProduct
import dao.Bank
import scala.concurrent.Future

@Singleton
class BankServiceImpl @Inject()(bankDAO: BankDAO) extends BankService {
  
  def createBank(bank: Bank): Future[Int] = {
    bankDAO.createBank(bank)
  }

  def updateBank(bank: Bank): Future[Int] = {
    bankDAO.updateBank(bank)
  }

  def getBankById(id: Int): Future[Option[Bank]] = {
    bankDAO.getBankById(id)
  }

  def getBankAll(): Future[List[Bank]] = {
    bankDAO.getBankAll()
  }

  def deleteBank(id: Int): Future[Int] = {
    bankDAO.deleteBank(id)
  }
  
  
  
   def create(bankProduct: BankProduct): Future[Int] = {
    bankDAO.create(bankProduct)
  }

  def update(bankProduct: BankProduct): Future[Int] = {
    bankDAO.update(bankProduct)
  }

  def getById(id: Int): Future[Option[BankProduct]] = {
    bankDAO.getById(id)
  }

  def getAll(): Future[List[BankProduct]] = {
    bankDAO.getAll()
  }

  def delete(id: Int): Future[Int] = {
    bankDAO.delete(id)
  }

  /**
   * Get bank and product using foreign key relationship
   */
  def getBankWithProduct(): Future[List[(Bank, BankProduct)]] = {
      bankDAO.getBankWithProduct()
    }

  /**
   * Get all bank and their product.It is possible some bank do not have their product
   */
  def getAllBankWithProduct(): Future[List[(Bank, Option[BankProduct])]] = {
      bankDAO.getAllBankWithProduct()
    }
  
}
  
