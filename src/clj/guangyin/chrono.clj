(ns guangyin.chrono
  (:require [guangyin.internal.fields :as fields]
            [guangyin.internal.types :refer :all])
  (:import (java.time.chrono HijrahDate JapaneseDate MinguoDate
                             ThaiBuddhistDate)))

(defn iso-era
  "Get ISO era.
   Examples:

     => (iso-era :bce)
     #<IsoEra BCE>
     => (iso-era :ce)
     #<IsoEra CE>"
  [key]
  (wrap (fields/get-field fields/iso-eras key)))

(defn hijrah-era
  "Get Hijri calendar era.
   Examples:

     => (hijrah-era :ah)
     #<HijrahEra AH>"
  [key]
  (wrap (fields/get-field fields/hijrah-eras key)))

(defn hijrah-date?
  "Returns true if the given value is a Hijri calendar date.
   Examples:

     => (hijrah-date? (hijrah-date :now))
     true
     => (hijrah-date? (minguo-date :now))
     false"
  [x]
  (instance? HijrahDate (unwrap x)))

(defprotocol IHijrahDate
  (hijrah-date [this] [year month day]
   "Coerce to Hijri calendar date.
    Examples:

      => (hijrah-date :now)
      #<HijrahDate Hijrah-umalqura AH 1436-06-28>
      => (hijrah-date (clock))
      #<HijrahDate Hijrah-umalqura AH 1436-06-28>
      => (hijrah-date (zone-id \"Asia/Shanghai\"))
      #<HijrahDate Hijrah-umalqura AH 1436-06-28>
      => (hijrah-date (local-date :now))
      #<HijrahDate Hijrah-umalqura AH 1436-06-28>
      => (hijrah-date 1436 6 28)
      #<HijrahDate Hijrah-umalqura AH 1436-06-28>"))

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
  "Get Japanese calendar era.
   Examples:

     => (japanese-era :heisei)
     #<JapaneseEra Heisei>
     => (japanese-era :meiji)
     #<JapaneseEra Meiji>
     => (japanese-era :showa)
     #<JapaneseEra Showa>
     => (japanese-era :taisho)
     #<JapaneseEra Taisho>"
  [key]
  (wrap (fields/get-field fields/japanese-eras key)))

(defn japanese-date?
  "Returns true if the given value is a Japanese calendar date.
   Examples:

     => (japanese-date? (japanese-date :now))
     true
     => (japanese-date? (minguo-date :now))
     false"
  [x]
  (instance? JapaneseDate (unwrap x)))

(defprotocol IJapaneseDate
  (japanese-date [this] [year month day] [era year month day]
   "Coerce to Japanese calendar date.
    Examples:

      => (japanese-date :now)
      #<JapaneseDate Japanese Heisei 27-04-17>
      => (japanese-date (clock))
      #<JapaneseDate Japanese Heisei 27-04-17>
      => (japanese-date (zone-id \"Asia/Shanghai\"))
      #<JapaneseDate Japanese Heisei 27-04-17>
      => (japanese-date (local-date :now))
      #<JapaneseDate Japanese Heisei 27-04-17>
      => (japanese-date 2015 4 17)
      #<JapaneseDate Japanese Heisei 27-04-17>
      => (japanese-date :showa 27 4 17)
      #<JapaneseDate Japanese Showa 27-04-17>
      => (japanese-date (japanese-era :showa) 27 4 17)
      #<JapaneseDate Japanese Showa 27-04-17>"))

(extend-protocol IJapaneseDate
  guangyin.internal.types.IWrapper
  (japanese-date ([this] (japanese-date @this))
                 ([era year month day] (japanese-date @era year month day)))
  clojure.lang.Keyword
  (japanese-date ([this] (when (= this :now)
                               (wrap (JapaneseDate/now))))
                 ([era year month day]
                  (wrap (JapaneseDate/of @(japanese-era era) year month day))))
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
  "Get Minguo calendar era.
   Examples:

     => (minguo-era :before-roc)
     #<MinguoEra BEFORE_ROC>
     => (minguo-era :roc)
     #<MinguoEra ROC>"
  [key]
  (wrap (fields/get-field fields/minguo-eras key)))

(defn minguo-date?
  "Returns true if the given value is a Minguo calendar date.
   Examples:

     => (minguo-date? (minguo-date :now))
     true
     => (minguo-date? (hijrah-date :now))
     false"
  [x]
  (instance? MinguoDate (unwrap x)))

(defprotocol IMinguoDate
  (minguo-date [this] [year month day]
   "Coerce to Minguo calendar date.
    Examples:

      => (minguo-date :now)
      #<MinguoDate Minguo ROC 104-04-17>
      => (minguo-date (clock))
      #<MinguoDate Minguo ROC 104-04-17>
      => (minguo-date (zone-id "Asia/Shanghai"))
      #<MinguoDate Minguo ROC 104-04-17>
      => (minguo-date (local-date :now))
      #<MinguoDate Minguo ROC 104-04-17>
      => (minguo-date 104 4 17)
      #<MinguoDate Minguo ROC 104-04-17>"))

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
  "Get Thai buddhist calendar era.
   Examples:

     => (thai-buddhist-era :before-be)
     #<ThaiBuddhistEra BEFORE_BE>
     => (thai-buddhist-era :be)
     #<ThaiBuddhistEra BE>"
  [key]
  (wrap (fields/get-field fields/thai-buddhist-eras key)))

(defn thai-buddhist-date?
  "Returns true if the given value is a Thai buddhist calendar date.
   Examples:

     => (thai-buddhist-date? (thai-buddhist-date :now))
     true
     => (thai-buddhist-date? (minguo-date :now))
     false"
  [x]
  (instance? ThaiBuddhistDate (unwrap x)))

(defprotocol IThaiBuddhistDate
  (thai-buddhist-date [this] [year month day]
   "Coerce to Thai buddhist calendar date.
    Examples:

      => (thai-buddhist-date :now)
      #<ThaiBuddhistDate ThaiBuddhist BE 2558-04-17>
      => (thai-buddhist-date (clock))
      #<ThaiBuddhistDate ThaiBuddhist BE 2558-04-17>
      => (thai-buddhist-date (zone-id \"Asia/Shanghai\"))
      #<ThaiBuddhistDate ThaiBuddhist BE 2558-04-17>
      => (thai-buddhist-date (local-date :now))
      #<ThaiBuddhistDate ThaiBuddhist BE 2558-04-17>
      => (thai-buddhist-date 2558 4 17)
      #<ThaiBuddhistDate ThaiBuddhist BE 2558-04-17>"))

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
