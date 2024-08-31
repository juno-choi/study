package accounts

import "errors"

type account struct {
	owner   string
	balnace int
}

var errNoMoney = errors.New("Can not withdraw!")

// create account
func NewAccount(owner string) *account {
	account := account{owner: owner, balnace: 0}
	return &account
}

// account deposit
func (a *account) Deposit(balnace int) {
	a.balnace = balnace
}

// account withdraw
func (a *account) Withdraw(amount int) error {
	if a.balnace < amount {
		return errNoMoney
	}
	a.balnace -= amount
	return nil
}

// get balance
func (a account) Balance() int {
	return a.balnace
}

// change of the account
func (a *account) ChangeOwner(newOwner string) {
	a.owner = newOwner
}

// get owner of the account
func (a account) Owner() string {
	return a.owner
}
