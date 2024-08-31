package mydict

import "errors"

// type of dictionary
type Dictionary map[string]string

var errNotFound = errors.New("not found!")
var errWordExists = errors.New("the word exists!")
var errCantUpdate = errors.New("cant update non-existing word")

// search for the dictionary
func (d Dictionary) Search(word string) (string, error) {
	value, exist := d[word]
	if exist {
		return value, nil
	}
	return "", errNotFound
}

// add of the dictionary
func (d Dictionary) Add(word, def string) error {
	_, err := d.Search(word)

	if err == errNotFound {
		d[word] = def
	} else if err == nil {
		return errWordExists
	}

	return nil
}

// update for the dictionary
func (d Dictionary) Update(word, definition string) error {
	_, err := d.Search(word)
	switch err {
	case nil:
		d[word] = definition
	case errNotFound:
		return errCantUpdate
	}
	return nil
}

// delte of the dictionary
func (d Dictionary) Delete(word string) {
	delete(d, word)
}
