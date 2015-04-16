(ns guangyin.internal.types-test
  (:require [clojure.test :refer :all]
            [guangyin.internal.fields :refer :all]
            [guangyin.internal.types :refer :all]))

(deftest test-wrapped
  (let [obj (Object.)
        wrapped (wrap obj)]
    (is (= obj @wrapped))
    (is (= obj (unwrap obj)))
    (is (= obj (unwrap wrapped)))
    (is (= wrapped (wrap wrapped)))
    (is (with-out-str (prn obj)) (with-out-str (prn wrapped)))))

(deftest test-date-time-format
  (let [dtf java.time.format.DateTimeFormatter/ISO_INSTANT
        wrapped (wrap dtf)
        str "1970-01-01T00:00:00Z"]
    (is (= dtf @wrapped))
    (is (= dtf (unwrap dtf)))
    (is (= dtf (unwrap wrapped)))
    (is (= str (wrapped (java.time.Instant/parse str))))))

(deftest test-temporal
  (let [date (java.time.LocalDate/parse "2015-01-10")
        newdate (java.time.LocalDate/parse "2016-01-10")
        wrapped (wrap date)]
    (is (= date @wrapped))
    (is (= date (unwrap date)))
    (is (= date (unwrap wrapped)))
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

(deftest test-temporal-accessor
  (let [month (java.time.Month/of 6)
        wrapped (wrap month)]
    (is (= month @wrapped))
    (is (= month (unwrap month)))
    (is (= month (unwrap wrapped)))
    (is (= 6 (:month-of-year wrapped)))
    (is (= nil (:year wrapped)))
    (is (= 42 (get wrapped :year 42)))
    (is (= true (contains? wrapped :month-of-year)))
    (is (= false (contains? wrapped :year)))
    (is (= :month-of-year (.key (find wrapped :month-of-year))))
    (is (= 6 (.val (find wrapped :month-of-year))))))

(deftest test-duration
  (let [duration (java.time.Duration/parse "PT1H2M3S")
        wrapped (wrap duration)]
    (is (= duration @wrapped))
    (is (= duration (unwrap duration)))
    (is (= duration (unwrap wrapped)))
    (is (= nil (:days wrapped)))
    (is (= 1 (:hours wrapped)))
    (is (= 62 (:minutes wrapped)))
    (is (= 3723 (:seconds wrapped)))
    (is (= 3723000 (:millis wrapped)))
    (is (= 3723000000 (:micros wrapped)))
    (is (= 3723000000000 (:nanos wrapped)))
    (is (= true (contains? wrapped :hours)))
    (is (= false (contains? wrapped :days)))
    (is (= :hours (.key (find wrapped :hours))))
    (is (= 1 (.val (find wrapped :hours))))))

(deftest test-temporal-amount
  (let [period (java.time.Period/parse "P1Y2M3D")
        wrapped (wrap period)]
    (is (= period @wrapped))
    (is (= period (unwrap period)))
    (is (= period (unwrap wrapped)))
    (is (= 1 (:years wrapped)))
    (is (= 2 (:months wrapped)))
    (is (= 3 (:days wrapped)))
    (is (= nil (:hours wrapped)))
    (is (= true (contains? wrapped :years)))
    (is (= false (contains? wrapped :hours)))
    (is (= :years (.key (find wrapped :years))))
    (is (= 1 (.val (find wrapped :years))))))

