(ns guangyin.internal.fields-test
  (:require [clojure.test :refer :all]
            [guangyin.internal.fields :refer [deffields defkeywords]])
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

(defkeywords java.time.DayOfWeek)
(deffields java.time.DayOfWeek)
(defkeywords ChronoUnit)
(deffields ChronoUnit)
(defkeywords ZoneOffsetTransitionRule$TimeDefinition)
(deffields ZoneOffsetTransitionRule$TimeDefinition)
(defkeywords DateTimeFormatter [ISO_INSTANT])

(deftest test-day-of-week-keywords
  (is (= nil (day-of-week-keywords :fraturday)))
  (is (= java.time.DayOfWeek/MONDAY (day-of-week-keywords :monday))))

(deftest test-day-of-week-fields
  (is (= :monday (day-of-week-fields java.time.DayOfWeek/MONDAY))))

(deftest test-chrono-unit-keywords
  (is (= nil (chrono-unit-keywords :half-decades)))
  (is (= ChronoUnit/DAYS (chrono-unit-keywords :days)))
  (is (= ChronoUnit/HALF_DAYS (chrono-unit-keywords :half-days))))

(deftest test-chrono-unit-fields
  (is (= :days (chrono-unit-fields ChronoUnit/DAYS)))
  (is (= :half-days (chrono-unit-fields ChronoUnit/HALF_DAYS))))

(deftest test-time-definition-keywords
  (is (= nil (chrono-unit-keywords :gmt)))
  (is (= ZoneOffsetTransitionRule$TimeDefinition/UTC (time-definition-keywords :utc))))

(deftest test-time-definition-fields
  (is (= :utc (time-definition-fields ZoneOffsetTransitionRule$TimeDefinition/UTC))))

(deftest test-date-time-formatter-keywords
  (is (= nil (date-time-formatter-keywords :iso-late)))
  (is (= DateTimeFormatter/ISO_INSTANT (date-time-formatter-keywords :iso-instant))))

