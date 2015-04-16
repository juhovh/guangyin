(ns guangyin.internal.fields)

(defn- field-to-symbol
  [class-var field]
  (let [class-name (.getName class-var)]
    (symbol (str class-name "/" (name field)))))

(defn- field-to-keyword
  [field]
  (-> (name field)
      (clojure.string/replace #"[^a-zA-Z0-9_]" "")
      (clojure.string/replace "_" "-")
      clojure.string/lower-case
      keyword))

(defn- class-info
  [class-symbol fields]
  (let [class-var (resolve class-symbol)
        class-name (.getSimpleName class-var)
        class-enums (.getEnumConstants class-var)
        class-fields (concat (map #(symbol (.name %)) class-enums) fields)
        class-keywords (map field-to-keyword class-fields)
        class-symbols (map #(field-to-symbol class-var %) class-fields)]
    {:name class-name
     :enums class-enums
     :fields class-fields
     :keywords class-keywords
     :symbols class-symbols}))

(defmacro defkeymap
  ([name class-symbol]
   `(defkeymap ~name ~class-symbol []))
  ([name class-symbol fields]
   (let [{:keys [symbols keywords]} (class-info class-symbol fields)]
     `(def ~name ~(zipmap keywords symbols)))))

(defkeymap months java.time.Month)
(defkeymap day-of-weeks java.time.DayOfWeek)
(defkeymap iso-eras java.time.chrono.IsoEra)
(defkeymap hijrah-eras java.time.chrono.HijrahEra)
(defkeymap minguo-eras java.time.chrono.MinguoEra)
(defkeymap thai-buddhist-eras java.time.chrono.ThaiBuddhistEra)
(defkeymap sign-styles java.time.format.SignStyle)
(defkeymap text-styles java.time.format.TextStyle)
(defkeymap format-styles java.time.format.FormatStyle)
(defkeymap resolver-styles java.time.format.ResolverStyle)
(defkeymap chrono-units java.time.temporal.ChronoUnit)
(defkeymap chrono-fields java.time.temporal.ChronoField)
(defkeymap time-definitions
  java.time.zone.ZoneOffsetTransitionRule$TimeDefinition)

(defkeymap durations java.time.Duration [ZERO])
(defkeymap instants java.time.Instant [MIN MAX EPOCH])
(defkeymap local-dates java.time.LocalDate [MIN MAX])
(defkeymap local-date-times java.time.LocalDateTime [MIN MAX])
(defkeymap local-times java.time.LocalTime [MIN MAX MIDNIGHT NOON])
(defkeymap offset-date-times java.time.OffsetDateTime [MIN MAX])
(defkeymap offset-times java.time.OffsetTime [MIN MAX])
(defkeymap periods java.time.Period [ZERO])
(defkeymap years java.time.Year [MIN_VALUE MAX_VALUE])
(defkeymap zone-offsets java.time.ZoneOffset [MIN MAX UTC])
(defkeymap japanese-eras java.time.chrono.JapaneseEra [HEISEI MEIJI
                                                       SHOWA TAISHO])
(defkeymap date-time-formatters
  java.time.format.DateTimeFormatter [BASIC_ISO_DATE ISO_LOCAL_DATE
				      ISO_OFFSET_DATE ISO_DATE ISO_LOCAL_TIME
                                      ISO_OFFSET_TIME ISO_TIME
                                      ISO_LOCAL_DATE_TIME ISO_OFFSET_DATE_TIME
                                      ISO_ZONED_DATE_TIME ISO_DATE_TIME
                                      ISO_ORDINAL_DATE ISO_WEEK_DATE ISO_INSTANT
                                      RFC_1123_DATE_TIME])
(defkeymap decimal-styles java.time.format.DecimalStyle [STANDARD])
(defkeymap iso-units java.time.temporal.IsoFields [QUARTER_YEARS
                                                   WEEK_BASED_YEARS])
(defkeymap iso-fields java.time.temporal.IsoFields [DAY_OF_QUARTER
                                                    QUARTER_OF_YEAR
                                                    WEEK_BASED_YEAR
                                                    WEEK_OF_WEEK_BASED_YEAR])
(defkeymap julian-fields java.time.temporal.JulianFields [JULIAN_DAY
                                                          MODIFIED_JULIAN_DAY
                                                          RATA_DIE])
(defkeymap week-fields java.time.temporal.WeekFields [ISO SUNDAY_START])


(def all-units (merge chrono-units iso-units))
(def all-fields (merge chrono-fields iso-fields julian-fields))

