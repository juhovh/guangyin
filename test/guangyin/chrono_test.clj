(ns guangyin.chrono-test
  (:require [clojure.test :refer :all]
            [guangyin.chrono :refer :all]))

(deftest test-iso-era
  (is @(iso-era :bce))
  (is @(iso-era :ce)))

(deftest test-hijrah-era
  (is @(hijrah-era :ah)))

(deftest test-japanese-era
  (is @(japanese-era :heisei))
  (is @(japanese-era :meiji))
  (is @(japanese-era :showa))
  (is @(japanese-era :taisho)))

(deftest test-minguo-era
  (is @(minguo-era :before-roc))
  (is @(minguo-era :roc)))

(deftest test-thai-buddhist-era
  (is @(thai-buddhist-era :before-be))
  (is @(thai-buddhist-era :be)))
