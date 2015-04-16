(ns guangyin.chrono
  (:require [guangyin.internal.fields :as fields]
            [guangyin.internal.types :refer :all])
  (:import (java.time.chrono HijrahDate JapaneseDate MinguoDate
                             ThaiBuddhistDate)))

(defn iso-era
  [key]
  (wrap (fields/get-field fields/iso-eras key)))

(defn hijrah-era
  [key]
  (wrap (fields/get-field fields/hijrah-eras key)))

(defprotocol IHijrahDate
  (hijrah-date [this] [year month day]))

(extend-protocol IHijrahDate
  guangyin.internal.types.IWrapper
  (hijrah-date [this] (hijrah-date @this))
  clojure.lang.Keyword
  (hijrah-date [this] (when (= this :now)
                            (wrap (HijrahDate/now))))
  java.time.temporal.TemporalAccessor
  (hijrah-date [this] (wrap (HijrahDate/from this)))
  java.time.Clock
  (hijrah-date [this] (wrap (HijrahDate/now this)))
  java.time.ZoneId
  (hijrah-date [this] (wrap (HijrahDate/now this)))
  java.lang.Long
  (hijrah-date [year month day] (wrap (HijrahDate/of year month day))))

(defn japanese-era
  [key]
  (wrap (fields/get-field fields/japanese-eras key)))

(defprotocol IJapaneseDate
  (japanese-date [this] [year month day] [era year month day]))

(extend-protocol IJapaneseDate
  guangyin.internal.types.IWrapper
  (japanese-date ([this] (japanese-date @this))
                 ([era year month day] (japanese-date @era year month day)))
  clojure.lang.Keyword
  (japanese-date [this] (when (= this :now)
                              (wrap (JapaneseDate/now))))
  java.time.temporal.TemporalAccessor
  (japanese-date [this] (wrap (JapaneseDate/from this)))
  java.time.chrono.JapaneseEra
  (japanese-date [era year month day]
    (wrap (JapaneseDate/of era year month day)))
  java.time.Clock
  (japanese-date [this] (wrap (JapaneseDate/now this)))
  java.time.ZoneId
  (japanese-date [this] (wrap (JapaneseDate/now this)))
  java.lang.Long
  (japanese-date [year month day] (wrap (JapaneseDate/of year month day))))

(defn minguo-era
  [key]
  (wrap (fields/get-field fields/minguo-eras key)))

(defprotocol IMinguoDate
  (minguo-date [this] [year month day]))

(extend-protocol IMinguoDate
  guangyin.internal.types.IWrapper
  (minguo-date [this] (minguo-date @this))
  clojure.lang.Keyword
  (minguo-date [this] (when (= this :now)
                            (wrap (MinguoDate/now))))
  java.time.temporal.TemporalAccessor
  (minguo-date [this] (wrap (MinguoDate/from this)))
  java.time.Clock
  (minguo-date [this] (wrap (MinguoDate/now this)))
  java.time.ZoneId
  (minguo-date [this] (wrap (MinguoDate/now this)))
  java.lang.Long
  (minguo-date [year month day] (wrap (MinguoDate/of year month day))))

(defn thai-buddhist-era
  [key]
  (wrap (fields/get-field fields/thai-buddhist-eras key)))

(defprotocol IThaiBuddhistDate
  (thai-buddhist-date [this] [year month day]))

(extend-protocol IThaiBuddhistDate
  guangyin.internal.types.IWrapper
  (thai-buddhist-date [this] (thai-buddhist-date @this))
  clojure.lang.Keyword
  (thai-buddhist-date [this] (when (= this :now)
                                   (wrap (ThaiBuddhistDate/now))))
  java.time.temporal.TemporalAccessor
  (thai-buddhist-date [this] (wrap (ThaiBuddhistDate/from this)))
  java.time.Clock
  (thai-buddhist-date [this] (wrap (ThaiBuddhistDate/now this)))
  java.time.ZoneId
  (thai-buddhist-date [this] (wrap (ThaiBuddhistDate/now this)))
  java.lang.Long
  (thai-buddhist-date [year month day]
    (wrap (ThaiBuddhistDate/of year month day))))
