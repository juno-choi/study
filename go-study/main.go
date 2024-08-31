package main

import (
	"errors"
	"fmt"
	"net/http"
)

var errRequestFail = errors.New("Request Fail")

func main() {
	var results = make(map[string]string)

	urls := []string{
		"https://www.google.com",
		"https://www.facebook.com",
		"https://www.naver.com",
	}

	for _, url := range urls {
		result := "OK"
		err := hitURL(url)
		if err != nil {
			result = "FAILED"
		}
		results[url] = result
	}
	for url, result := range results {
		fmt.Println(url, result)
	}
}

func hitURL(url string) error {
	fmt.Println("check url :", url)
	resp, err := http.Get(url)
	if err != nil || resp.StatusCode >= 400 {
		return errRequestFail
	}
	return nil
}
