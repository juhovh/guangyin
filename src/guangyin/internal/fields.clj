(ns guangyin.internal.fields)

(defn- camel-to-kebab
  [name]
  (-> name
      (clojure.string/replace #"(.)([A-Z][a-z]+)" "$1-$2")
      (clojure.string/replace #"([a-z0-9])([A-Z])" "$1-$2")
      (clojure.string/lower-case)))

(defn- field-to-symbol
  [class-var field]
  (let [decl-class (if (instance? java.lang.Enum class-var)
                       (.getDeclaringClass class-var)
                       class-var)
        class-name (.getName decl-class)]
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
        class-title (camel-to-kebab class-name)
        class-enums (.getEnumConstants class-var)
        class-fields (concat (map #(symbol (.name %)) class-enums) fields)
        class-keywords (map field-to-keyword class-fields)
        class-symbols (map #(field-to-symbol class-var %) class-fields)]
    {:name class-name
     :title class-title
     :enums class-enums
     :fields class-fields
     :keywords class-keywords
     :symbols class-symbols}))

(defmacro defkeywords
  ([class-symbol]
   `(defkeywords ~class-symbol []))
  ([class-symbol fields]
   (let [{:keys [title symbols keywords]} (class-info class-symbol fields)
         keywords-sym (symbol (str title "-keywords"))]
     `(def ~keywords-sym ~(zipmap keywords symbols)))))

(defmacro deffields
  ([class-symbol]
   `(deffields ~class-symbol []))
  ([class-symbol fields]
   (let [{:keys [title symbols keywords]} (class-info class-symbol fields)
         fields-sym (symbol (str title "-fields"))]
     `(def ~fields-sym ~(zipmap symbols keywords)))))

(defkeywords java.time.Month)
(defkeywords java.time.DayOfWeek)
(defkeywords java.time.chrono.IsoEra)
(defkeywords java.time.chrono.HijrahEra)
(defkeywords java.time.chrono.MinguoEra)
(defkeywords java.time.chrono.ThaiBuddhistEra)
(defkeywords java.time.format.SignStyle)
(defkeywords java.time.format.TextStyle)
(defkeywords java.time.format.FormatStyle)
(defkeywords java.time.format.ResolverStyle)
(defkeywords java.time.temporal.ChronoUnit)
(defkeywords java.time.temporal.ChronoField)
(defkeywords java.time.zone.ZoneOffsetTransitionRule$TimeDefinition)

(deffields java.time.Month)
(deffields java.time.DayOfWeek)
(deffields java.time.chrono.IsoEra)
(deffields java.time.chrono.HijrahEra)
(deffields java.time.chrono.MinguoEra)
(deffields java.time.chrono.ThaiBuddhistEra)
(deffields java.time.format.SignStyle)
(deffields java.time.format.TextStyle)
(deffields java.time.format.FormatStyle)
(deffields java.time.format.ResolverStyle)
(deffields java.time.temporal.ChronoUnit)
(deffields java.time.temporal.ChronoField)
(deffields java.time.zone.ZoneOffsetTransitionRule$TimeDefinition)

(defkeywords java.time.Duration [ZERO])
(defkeywords java.time.Instant [MIN MAX EPOCH])
(defkeywords java.time.LocalDate [MIN MAX])
(defkeywords java.time.LocalDateTime [MIN MAX])
(defkeywords java.time.LocalTime [MIN MAX MIDNIGHT NOON])
(defkeywords java.time.OffsetDateTime [MIN MAX])
(defkeywords java.time.OffsetTime [MIN MAX])
(defkeywords java.time.Period [ZERO])
(defkeywords java.time.Year [MIN_VALUE MAX_VALUE])
(defkeywords java.time.ZoneOffset [MIN MAX UTC])
(defkeywords java.time.format.DateTimeFormatter [BASIC_ISO_DATE ISO_LOCAL_DATE
                                                 ISO_OFFSET_DATE ISO_DATE
                                                 ISO_LOCAL_TIME ISO_OFFSET_TIME
                                                 ISO_TIME ISO_LOCAL_DATE_TIME
                                                 ISO_OFFSET_DATE_TIME
                                                 ISO_ZONED_DATE_TIME
                                                 ISO_DATE_TIME ISO_ORDINAL_DATE
                                                 ISO_WEEK_DATE ISO_INSTANT
                                                 RFC_1123_DATE_TIME])
(defkeywords java.time.format.DecimalStyle [STANDARD])
(defkeywords java.time.temporal.IsoFields [DAY_OF_QUARTER QUARTER_OF_YEAR
                                           QUARTER_YEARS WEEK_BASED_YEAR
                                           WEEK_BASED_YEARS
                                           WEEK_OF_WEEK_BASED_YEAR])
(defkeywords java.time.temporal.JulianFields [JULIAN_DAY MODIFIED_JULIAN_DAY
                                              RATA_DIE])
(defkeywords java.time.temporal.WeekFields [ISO SUNDAY_START WEEK_BASED_YEARS])

