package com.cg.ibs.cardmanagement.ui;

import java.util.*;
import java.util.regex.Pattern;

import com.cg.ibs.cardmanagement.bean.CaseIdBean;
import com.cg.ibs.cardmanagement.bean.CreditCardBean;
import com.cg.ibs.cardmanagement.bean.CreditCardTransaction;
import com.cg.ibs.cardmanagement.bean.DebitCardBean;
import com.cg.ibs.cardmanagement.bean.DebitCardTransaction;
import com.cg.ibs.cardmanagement.exceptionhandling.IBSException;
import com.cg.ibs.cardmanagement.service.BankService;
import com.cg.ibs.cardmanagement.service.BankServiceClassImpl;
import com.cg.ibs.cardmanagement.service.CustomerService;
import com.cg.ibs.cardmanagement.service.CustomerServiceImpl;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CardManagementUI {

	static BigInteger accountNumber = null;
	static Scanner scan;
	static BigInteger debitCardNumber = null;
	static BigInteger creditCardNumber = null;
	static int userInput = -1;
	static int ordinal = -1;
	static String type = null;
	static String transactionId;
	static boolean success = false;
	static int myChoice = -1;
	static int pin=-1;
	boolean check = false;
	CustomerService customService = new CustomerServiceImpl();
	BankService bankService = new BankServiceClassImpl();

	public void doIt() {
		while (true) {
			success = false;
			System.out.println("Welcome to card management System");
			System.out.println("Enter 1 to login as a customer");
			System.out.println("Enter 2 to login as a bank admin");

			while (!success) {

				try {

					userInput = scan.nextInt();
					success = true;
				} catch (InputMismatchException wrongFormat) {
					scan.next();
					System.out.println("Enter between 1 or 2");

				}
			}

			if (userInput == 1) {

				System.out.println("You are logged in as a customer");
				CustomerMenu choice = null;
				while (choice != CustomerMenu.CUSTOMER_LOG_OUT) {

					System.out.println("Menu");
					System.out.println("--------------------");
					System.out.println("Choice");
					System.out.println("--------------------");
					for (CustomerMenu mmenu : CustomerMenu.values()) {
						System.out.println(mmenu.ordinal() + "\t" + mmenu);
					}
					System.out.println("Choice");
					success = false;
					while (!success) {

						try {
							ordinal = scan.nextInt();
							success = true;
						} catch (InputMismatchException wrongFormat) {
							scan.next();
							System.out.println("Choose a valid option");
						}
					}
					if (ordinal >= 0 && ordinal < 15) {
						choice = CustomerMenu.values()[ordinal];

						BigInteger creditCardNumber = null;
						switch (choice) {

						case LIST_EXISTING_DEBIT_CARDS:

							List<DebitCardBean> debitCardBeans = customService.viewAllDebitCards();
							if (debitCardBeans.isEmpty()) {
								System.out.println("No Existing Debit Cards");
							} else {
								for (DebitCardBean debitCardBean : debitCardBeans) {
									System.out.println(debitCardBeans.toString());
								}
							}
							break;

						case LIST_EXISTING_CREDIT_CARDS:

							List<CreditCardBean> creditCardBeans = customService.viewAllCreditCards();
							if (creditCardBeans.isEmpty()) {
								System.out.println("No Existing Credit Cards");
							} else {
								for (CreditCardBean creditCardBean : creditCardBeans) {
									System.out.println(creditCardBeans.toString());
								}
							}
							break;

						case APPLY_NEW_DEBIT_CARD:
							success = false;
							System.out.println("You are applying for a new Debit Card");
							while (!success) {

								try {
									System.out.println("Enter Account Number you want to apply debit card for :");

									accountNumber = scan.nextBigInteger();

									success = true;
								} catch (InputMismatchException wrongFormat) {

									scan.next();
									System.out.println("Renter 10 digit account number");
								}
							}

							boolean result = customService.applyNewDebitCard(accountNumber);

							if (result) {
								System.out.println(" Debit Card applied successfully");
							} else {
								System.out.println("Account Number Entered wrong");
							}

							break;
						case APPLY_NEW_CREDIT_CARD:

							System.out.println("You are applying for a new Credit Card");

							customService.applyNewCreditCard();
							System.out.println("New Credit Card applied successfully");
							break;

						case UPGRADE_EXISTING_DEBIT_CARD:

							success = false;

							System.out.println("Enter your Debit Card Number: ");
							while (!success) {

								try {

									debitCardNumber = scan.nextBigInteger();
									type = customService.verifyDebitcardType(debitCardNumber);
									if (type == null) {
										throw new NullPointerException();
									}

									success = true;
								} catch (InputMismatchException wrongFormat) {
									scan.next();
									System.out.println("Enter a valid Debit card number");
									continue;

								} catch (NullPointerException notFound) {
									System.out.println("Debit Card not Found");
									scan.next();

									continue;
								}
								if (type.equals("Silver")) {
									System.out.println("Choose 1 to upgrade to Gold");
									System.out.println("Choose 2 to upgrade to Platinum");

									success = false;
									while (!success) {
										try {
											myChoice = scan.nextInt();
											success = true;
										} catch (InputMismatchException wrongFormat) {
											scan.next();
											System.out.println("Choose between 1 or 2");

										}
									}
									System.out.println(customService.requestDebitCardUpgrade(debitCardNumber, myChoice));

								} else if (type.equals("Gold")) {
									System.out.println("Choose 2 to upgrade to Platinum");
									success = false;
									while (!success) {
										try {
											myChoice = scan.nextInt();
											success = true;
										} catch (InputMismatchException wrongFormat) {
											scan.next();
											System.out.println("Enter 2 to upgrade");
										}
									}
									System.out
											.println(customService.requestDebitCardUpgrade(debitCardNumber, myChoice));

								} else {
									System.out.println("You already have a Platinum Card");
								}

							}

							break;
						case UPGRADE_EXISTING_CREDIT_CARD:
							success = false;

							System.out.println("Enter your Credit Card Number: ");
							while (!success) {

								try {

									creditCardNumber = scan.nextBigInteger();

									type = customService.verifyCreditcardType(creditCardNumber);
									if (type == null) {
										throw new NullPointerException();
									}

									success = true;
								} catch (InputMismatchException wrongFormat) {
									scan.next();
									System.out.println("Enter a valid Credit card number");
									continue;

								} catch (NullPointerException notFound) {
									System.out.println("Credit Card not Found");
									scan.next();

									continue;
								}
								if (type.equals("Silver")) {
									System.out.println("Choose 1 to upgrade to Gold");
									System.out.println("Choose 2 to upgrade to Platinum");

									success = false;
									while (!success) {
										try {
											myChoice = scan.nextInt();
											success = true;
										} catch (InputMismatchException wrongFormat) {
											scan.next();
											System.out.println("Choose between 1 or 2");

										}
									}
									System.out.println(
											customService.requestCreditCardUpgrade(creditCardNumber, myChoice));

								} else if (type.equals("Gold")) {
									System.out.println("Choose 2 to upgrade to Platinum");
									success = false;
									while (!success) {
										try {
											myChoice = scan.nextInt();
											success = true;
										} catch (InputMismatchException wrongFormat) {
											scan.next();
											System.out.println("Enter 2 to upgrade");
										}
									}
									System.out.println(
											customService.requestCreditCardUpgrade(creditCardNumber, myChoice));

								} else {
									System.out.println("You already have a Platinum Card");
								}

							}

							break;

						case RESET_DEBIT_CARD_PIN:
							success = false;
							System.out.println("Enter your Debit Card Number: ");

							while (!success) {

								try {
									debitCardNumber = scan.nextBigInteger();
									check = customService.verifyDebitCardNumber(debitCardNumber);
									if (!check)
										throw new IBSException(" Debit Card Number does not exist");
									success = true;
								} catch (InputMismatchException wrongFormat) {
									scan.next();
									System.out.println("Enter a valid debit card number");
								} catch (IBSException newException) {
									System.out.println(newException.getMessage());
								}

							}

							if (check) {
								System.out.println("Enter your existing pin:");

								success = false;
								while (!success) {
									try {

										pin = scan.nextInt();

										if (customService.getLength(pin) != 4)
											throw new IBSException("Incorrect Length of pin ");
										getNewPin(pin, debitCardNumber);

										// cll function below to set new pin
										success = true;
									} catch (InputMismatchException wrongFormat) {
										System.out.println("Enter a valid 4 digit pin");
										scan.next();
										continue;

									} catch (IBSException ExceptionObj) {
										System.out.println(ExceptionObj.getMessage());
										scan.next();
										continue;

									}
								}
							}

							break;
						case RESET_CREDIT_CARD_PIN:
							success = false;
							System.out.println("Enter your Credit Card Number: ");
							while (!success) {
								try {
									creditCardNumber = scan.nextBigInteger();
									check = customService.verifyCreditCardNumber(creditCardNumber);
									if (!check)
										throw new IBSException("Credit Card Number does not exist");
									success = true;
								} catch (InputMismatchException wrongFormat) {
									scan.next();
									System.out.println("Enter a valid credit card number");
								} catch (IBSException newException) {
									System.out.println(newException.getMessage());
								}
							}

							if (check) {
								System.out.println("Enter your existing pin:");
								success = false;
								while (!success) {
									try {

										int pin = scan.nextInt();

										if (customService.getLength(pin) != 4)
											throw new IBSException("Incorrect Length of pin ");
										getNewPin2(pin, creditCardNumber);
										// cll function below to set new pin
										success = true;
									} catch (InputMismatchException wrongFormat) {
										System.out.println("Enter a valid 4 digit pin");
										scan.next();

									} catch (IBSException ExceptionObj) {
										System.out.println(ExceptionObj.getMessage());
										scan.next();

									}
								}

							}
							break;

						case REPORT_DEBIT_CARD_LOST:

							success = false;
							while (!success) {

								try {

									System.out.println("Enter your Debit Card Number: ");
									debitCardNumber = scan.nextBigInteger();
									check = customService.requestDebitCardLost(debitCardNumber);
									if (check == false)
										throw new IBSException("Wrong Debit Card Number");
									if (check) {
										System.out.println("Ticket raised successfully");
									} else {
										System.out.println("Invalid Debit card number");
									}
									success = true;
								} catch (InputMismatchException wrongFormat) {

									System.out.println("Not a valid format");
									scan.next();
									continue;
								} catch (IBSException e) {
									System.out.println(e.getMessage());
									continue;
								}
							}

							break;

						case REPORT_CREDIT_CARD_LOST:
							success = false;
							while (!success) {

								try {

									System.out.println("Enter your Credit Card Number: ");
									creditCardNumber = scan.nextBigInteger();
									check = customService.requestCreditCardLost(creditCardNumber);
									if (check == false)
										throw new IBSException("Wrong credit Card Number");
									if (check) {
										System.out.println("Ticket raised successfully");
									} else {
										System.out.println("Invalid Credit card number");
									}
									success = true;
								} catch (InputMismatchException wrongFormat) {

									System.out.println("Not a valid format");
									scan.next();
									continue;
								} catch (IBSException e) {
									System.out.println(e.getMessage());
									continue;
								}
							}

							break;

						case REQUEST_DEBIT_CARD_STATEMENT:
							success = false;
							int days = 0;
							while (!success) {

								try {
									System.out.println("Enter your Debit Card Number: ");
									debitCardNumber = scan.nextBigInteger();
									check = customService.verifyDebitCardNumber(debitCardNumber);
									if (!check)
										throw new IBSException(" Debit Card Number does not exist");
									success = true;
								} catch (InputMismatchException wrongFormat) {
									scan.next();
									System.out.println("Not a valid format");
								} catch (IBSException newException) {
									System.out.println(newException.getMessage());
									continue;
								}
							}
							success = false;

							while (!success) {
								try {
									System.out.println("enter days : ");
									days = scan.nextInt();
									success = true;
								} catch (InputMismatchException wrongFormat) {
									scan.next();
									System.out.println("Not a valid format");
								}
							}

							try {
								List<DebitCardTransaction> debitCardBeanTrns = customService.getDebitTrns(days,
										debitCardNumber);
								for (DebitCardTransaction debitCardTrns : debitCardBeanTrns)
									System.out.println(debitCardTrns.toString());
								if (debitCardBeanTrns.size() == 0)
									throw new IBSException(" No Transactions");
							}

							catch (IBSException e) {
								System.out.println(e.getMessage());
							}

							break;
						case REQUEST_CREDIT_CARD_STATEMENT:
							success = false;
							int days1 = 0;
							while (!success) {

								try {
									System.out.println("Enter your Credit Card Number: ");
									creditCardNumber = scan.nextBigInteger();
									check = customService.verifyCreditCardNumber(creditCardNumber);
									if (!check)
										throw new IBSException(" Credit Card Number does not exist");
									success = true;
								} catch (InputMismatchException wrongFormat) {
									scan.next();
									System.out.println("Not a valid format");
									continue;
								} catch (IBSException newException) {
									System.out.println(newException.getMessage());
									continue;
								}
							}
							success = false;

							while (!success) {
								try {
									System.out.println("enter days : ");
									days1 = scan.nextInt();
									success = true;
								} catch (InputMismatchException wrongFormat) {
									scan.next();
									System.out.println("Not a valid format");
								}
							}

							try {
								List<CreditCardTransaction> creditCardBeanTrns = customService.getCreditTrans(days1,
										creditCardNumber);
								for (CreditCardTransaction creditCardTrns : creditCardBeanTrns)
									System.out.println(creditCardTrns.toString());
								if (creditCardBeanTrns.size() == 0)
									throw new IBSException("NO Transactions");
							}

							catch (IBSException e) {
								System.out.println(e.getMessage());
							}

							break;
						case REPORT_DEBITCARD_STATEMENT_MISMATCH:

							success = false;
							while (!success) {

								try {

									System.out.println("Enter your transaction id");
									transactionId = scan.next();
									check = customService.raiseDebitMismatchTicket(transactionId);
									if (!check)
										throw new IBSException("Transaction ID not found");
									success = true;
								} catch (InputMismatchException wrongFormat) {
									scan.next();
									System.out.println("Not a valid format");
									continue;
								} catch (IBSException e) {
									System.out.println(e.getMessage());
									continue;
								}
							}

							if (check) {
								System.out.println(" Ticket Raised successfully");
							} else {
								System.out.println("Invalid transaction id");
							}

							break;
						case REPORT_CREDITCARD_STATEMENT_MISMATCH:
							success = false;
							while (!success) {

								try {

									System.out.println("Enter your transaction id");
									transactionId = scan.next();
									check = customService.raiseCreditMismatchTicket(transactionId);
									if (!check)
										throw new IBSException("Transaction ID not found");
									success = true;
								} catch (InputMismatchException wrongFormat) {
									scan.next();
									System.out.println("Not a valid format");
								} catch (IBSException e) {
									System.out.println(e.getMessage());
									continue;
								}
							}

							if (check) {
								System.out.println(" Ticket Raised successfully");
							} else {
								System.out.println("Invalid transaction id");
							}

							break;
						case CUSTOMER_LOG_OUT:
							System.out.println("LOGGED OUT");
							break;
						}
					}

				}
			} else {
				if (userInput == 2) {

					System.out.println("You are logged in as a Bank Admin");
					BankMenu cchoice = null;
					while (cchoice != BankMenu.BANK_LOG_OUT) {
						System.out.println("Menu");
						System.out.println("--------------------");
						System.out.println("Choice");
						System.out.println("--------------------");
						for (BankMenu mmenu : BankMenu.values()) {
							System.out.println(mmenu.ordinal() + "\t" + mmenu);
						}
						System.out.println("Choice");
						success = false;
						while (!success) {
							try {
								ordinal = scan.nextInt();
								success = true;
							} catch (InputMismatchException wrongFormat) {
								scan.next();
								System.out.println("Enter a valid  option");
							}
						}

						if (ordinal >= 0 && ordinal < BankMenu.values().length) {
							cchoice = BankMenu.values()[ordinal];

							switch (cchoice) {

							case LIST_QUERIES:

								List<CaseIdBean> caseBeans = bankService.viewQueries();
								if (caseBeans.isEmpty()) {
									System.out.println("No Existing Queries");
								} else {

									for (CaseIdBean caseId : caseBeans) {

										System.out.println(caseId.toString());
									}
								}

								break;

							case REPLY_QUERIES:
								String queryId = null;
								String newStatus = null;
								success = false;
								while (!success) {
									try {
										System.out.println("Enter query ID ");
										queryId = scan.next();
										check = bankService.verifyQueryId(queryId);
										if (!check)
											throw new IBSException("Invalid Query Id");

										success = true;
									} catch (InputMismatchException wrongFormat) {
										scan.next();
										System.out.println("Not a valid format");
										continue;
									} catch (IBSException ibs) {
										System.out.println(ibs.getMessage());
										continue;
									}
								}
								if (check) {

									System.out.println("Enter new Status");
									newStatus = scan.next();
									bankService.setQueryStatus(queryId, newStatus);
								}

								else {
									System.out.println("Invalid query id");
								}

								break;
							case VIEW_DEBIT_CARD_STATEMENT:
								success = false;
								int days = 0;
								while (!success) {

									try {
										System.out.println("Enter your Debit Card Number: ");
										debitCardNumber = scan.nextBigInteger();
										check = customService.verifyDebitCardNumber(debitCardNumber);
										if (!check)
											throw new IBSException(" Debit Card Number does not exist");
										success = true;
									} catch (InputMismatchException wrongFormat) {
										scan.next();
										System.out.println("Not a valid format");
									} catch (IBSException newException) {
										System.out.println(newException.getMessage());
										continue;
									}
								}
								success = false;

								while (!success) {
									try {
										System.out.println("enter days : ");
										days = scan.nextInt();
										success = true;
									} catch (InputMismatchException wrongFormat) {
										scan.next();
										System.out.println("Not a valid format");
									}
								}

								try {
									List<DebitCardTransaction> debitCardBeanTrns = customService.getDebitTrns(days,
											debitCardNumber);
									for (DebitCardTransaction debitCardTrns : debitCardBeanTrns)
										System.out.println(debitCardTrns.toString());
									if (debitCardBeanTrns.size() == 0)
										throw new IBSException(" No Transactions");
								}

								catch (IBSException e) {
									System.out.println(e.getMessage());
								}

								break;
							case VIEW_CREDIT_CARD_STATEMENT:
								success = false;
								int days1 = 0;
								while (!success) {

									try {
										System.out.println("Enter your Credit Card Number: ");
										creditCardNumber = scan.nextBigInteger();
										check = customService.verifyCreditCardNumber(creditCardNumber);
										if (!check)
											throw new IBSException(" Credit Card Number does not exist");
										success = true;
									} catch (InputMismatchException wrongFormat) {
										scan.next();
										System.out.println("Not a valid format");
										continue;
									} catch (IBSException newException) {
										System.out.println(newException.getMessage());
										continue;
									}
								}
								success = false;

								while (!success) {
									try {
										System.out.println("enter days : ");
										days1 = scan.nextInt();
										success = true;
									} catch (InputMismatchException wrongFormat) {
										scan.next();
										System.out.println("Not a valid format");
									}
								}

								try {
									List<CreditCardTransaction> creditCardBeanTrns = customService.getCreditTrans(days1,
											creditCardNumber);
									for (CreditCardTransaction creditCardTrns : creditCardBeanTrns)
										System.out.println(creditCardTrns.toString());
									if (creditCardBeanTrns.size() == 0)
										throw new IBSException("NO Transactions");
								}

								catch (IBSException e) {
									System.out.println(e.getMessage());
								}

								break;
							case BANK_LOG_OUT:
								System.out.println("LOGGED OUT");
								break;

							}
						}
					}
				} else {
					System.out.println("Invalid Option!!");

				}

			}

		}
	}

	private void getNewPin(int pin, BigInteger debitCardNumber) {
		if (customService.verifyDebitPin(pin, debitCardNumber)) {
			System.out.println("Enter new pin");
			success = false;
			while (!success) {
				try {

					pin = scan.nextInt();

					if (customService.getLength(pin) != 4)
						throw new IBSException("Incorrect Length of pin ");
					System.out.println(customService.resetDebitPin(debitCardNumber, pin));
					success = true;
				} catch (InputMismatchException wrongFormat) {
					System.out.println("Enter a valid 4 digit pin");
					scan.next();
					continue;

				} catch (IBSException ExceptionObj) {
					System.out.println(ExceptionObj.getMessage());
					scan.next();
					continue;

				}
			}

		} else {

			System.out.println("You have entered wrong pin ");
			System.out.println("Try again");
		}

	}

	private void getNewPin2(int pin, BigInteger creditCardNumber) {
		if (customService.verifyCreditPin(pin, creditCardNumber)) {
			System.out.println("Enter new pin");
			success = false;
			while (!success) {
				try {

					 pin = scan.nextInt();

					if (customService.getLength(pin) != 4)
						throw new IBSException("Incorrect Length of pin ");
					System.out.println(customService.resetCreditPin(creditCardNumber, pin));
					success = true;
				} catch (InputMismatchException wrongFormat) {
					System.out.println("Enter a valid 4 digit pin");
					scan.next();
					continue;
				} catch (IBSException ExceptionObj) {
					System.out.println(ExceptionObj.getMessage());
					scan.next();
					continue;
				}
			}

		} else {

			System.out.println("You have entered wrong pin ");
			System.out.println("Try again");
		}

	}

	public static void main(String args[]) throws Exception {
		scan = new Scanner(System.in);
		CardManagementUI obj = new CardManagementUI();
		obj.doIt();
		System.out.println("Program End");
		obj.scan.close();
	}
}