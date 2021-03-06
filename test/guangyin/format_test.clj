(ns guangyin.format-test
  (:require [clojure.test :refer :all]
            [guangyin.format :refer :all]))

(deftest test-date-time-formatter
  (is (date-time-formatter? @(date-time-formatter :iso-instant)))
  (is (date-time-formatter? @(date-time-formatter (date-time-formatter :iso-instant))))
  (is (date-time-formatter? @(date-time-formatter "yyyy-MM-dd" java.util.Locale/US))))
