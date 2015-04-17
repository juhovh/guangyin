(ns guangyin.chrono-test
  (:require [clojure.test :refer :all]
            [guangyin.core :refer :all]
            [guangyin.chrono :refer :all]))

(deftest test-iso-era
  (is @(iso-era :bce))
  (is @(iso-era :ce)))

(deftest test-hijrah-era
  (is @(hijrah-era :ah)))

(deftest test-hijrah-date
  (is (hijrah-date? @(hijrah-date :now)))
  (is (hijrah-date? @(hijrah-date (clock))))
  (is (hijrah-date? @(hijrah-date (zone-id "Asia/Shanghai"))))
  (is (hijrah-date? @(hijrah-date (local-date :now))))
  (is (hijrah-date? @(hijrah-date 1436 6 28))))

(deftest test-japanese-era
  (is @(japanese-era :heisei))
  (is @(japanese-era :meiji))
  (is @(japanese-era :showa))
  (is @(japanese-era :taisho)))

(deftest test-japanese-date
  (is (japanese-date? @(japanese-date :now)))
  (is (japanese-date? @(japanese-date (clock))))
  (is (japanese-date? @(japanese-date (zone-id "Asia/Shanghai"))))
  (is (japanese-date? @(japanese-date (local-date :now))))
  (is (japanese-date? @(japanese-date 2015 4 17)))
  (is (japanese-date? @(japanese-date :showa 27 4 17)))
  (is (japanese-date? @(japanese-date (japanese-era :showa) 27 4 17))))

(deftest test-minguo-era
  (is @(minguo-era :before-roc))
  (is @(minguo-era :roc)))

(deftest test-minguo-date
  (is (minguo-date? @(minguo-date :now)))
  (is (minguo-date? @(minguo-date (clock))))
  (is (minguo-date? @(minguo-date (zone-id "Asia/Shanghai"))))
  (is (minguo-date? @(minguo-date (local-date :now))))
  (is (minguo-date? @(minguo-date 104 4 17))))

(deftest test-thai-buddhist-era
  (is @(thai-buddhist-era :before-be))
  (is @(thai-buddhist-era :be)))

(deftest test-thai-buddhist-date
  (is (thai-buddhist-date? @(thai-buddhist-date :now)))
  (is (thai-buddhist-date? @(thai-buddhist-date (clock))))
  (is (thai-buddhist-date? @(thai-buddhist-date (zone-id "Asia/Shanghai"))))
  (is (thai-buddhist-date? @(thai-buddhist-date (local-date :now))))
  (is (thai-buddhist-date? @(thai-buddhist-date 2558 4 17))))


