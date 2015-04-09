(ns guangyin.internal.fields-test
  (:require [clojure.test :refer :all]
            [guangyin.internal.fields :refer :all])
  (:import (java.time.temporal ChronoUnit)
           (java.time.format DateTimeFormatter)
           (java.time.zone ZoneOffsetTransitionRule$TimeDefinition)))

(defn refer-private [ns]
  (doseq [[symbol var] (ns-interns ns)]
    (when (:private (meta var))
      (intern *ns* symbol var))))

(refer-private 'guangyin.internal.fields)

(deftest test-camel-to-kebab
  (is (= "camel-case" (camel-to-kebab "CamelCase")))
  (is (= "camel-camel-case" (camel-to-kebab "CamelCamelCase")))
  (is (= "camel2-camel2-case" (camel-to-kebab "Camel2Camel2Case")))
  (is (= "get-http-response-code" (camel-to-kebab "getHTTPResponseCode")))
  (is (= "get2-http-response-code" (camel-to-kebab "get2HTTPResponseCode")))
  (is (= "http-response-code" (camel-to-kebab "HTTPResponseCode")))
  (is (= "http-response-code-xyz" (camel-to-kebab "HTTPResponseCodeXYZ"))))
 
(deftest test-field-to-symbol
  (is (= (symbol "java.time.DayOfWeek/MONDAY") (field-to-symbol java.time.DayOfWeek 'MONDAY)))
  (is (= (symbol "java.time.temporal.ChronoUnit/DAYS") (field-to-symbol ChronoUnit 'DAYS)))
  (is (= (symbol "java.time.temporal.ChronoUnit/HALF_DAYS") (field-to-symbol ChronoUnit 'HALF_DAYS)))
  (is (= (symbol "java.time.zone.ZoneOffsetTransitionRule$TimeDefinition/UTC") (field-to-symbol ZoneOffsetTransitionRule$TimeDefinition 'UTC))))

(deftest test-day-of-weeks
  (is (= nil (day-of-weeks :fraturday)))
  (is (= java.time.DayOfWeek/MONDAY (day-of-weeks :monday))))

(deftest test-chrono-units
  (is (= nil (chrono-units :half-decades)))
  (is (= ChronoUnit/DAYS (chrono-units :days)))
  (is (= ChronoUnit/HALF_DAYS (chrono-units :half-days))))

(deftest test-time-definition-keywords
  (is (= nil (chrono-units :gmt)))
  (is (= ZoneOffsetTransitionRule$TimeDefinition/UTC (time-definitions :utc))))

(deftest test-date-time-formatters
  (is (= nil (date-time-formatters :iso-late)))
  (is (= DateTimeFormatter/ISO_INSTANT (date-time-formatters :iso-instant))))

