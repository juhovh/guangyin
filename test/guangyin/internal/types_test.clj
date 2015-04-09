(ns guangyin.internal.types-test
  (:require [clojure.test :refer :all]
            [guangyin.internal.fields :refer :all]
            [guangyin.internal.types :refer :all]))

(deftest test-temporal
  (let [date (java.time.LocalDate/parse "2015-01-10")
        newdate (java.time.LocalDate/parse "2016-01-10")
        wrapped (wrap-temporal all-iso-fields date)]
    (is (= date @wrapped))
    (is (= 2015 (:year wrapped)))
    (is (= 1 (:month-of-year wrapped)))
    (is (= 10 (:day-of-month wrapped)))
    (is (= nil (:minute-of-hour wrapped)))
    (is (= 42 (get wrapped :minute-of-hour 42)))
    (is (= true (contains? wrapped :year)))
    (is (= false (contains? wrapped :minute-of-hour)))
    (is (= :year (.key (find wrapped :year))))
    (is (= 2015 (.val (find wrapped :year))))
    (is (= newdate @(assoc wrapped :year 2016)))))
