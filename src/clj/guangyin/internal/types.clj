(ns guangyin.internal.types
  (:import (guangyin.internal.types ObjectWrapper TemporalAccessorWrapper TemporalWrapper)
           (java.time.temporal TemporalAccessor Temporal)))

(defmethod print-method ObjectWrapper
  [o ^java.io.Writer w]
  (.write w (str "#<" (.getSimpleName (class @o)) " " (str o) ">")))

(defmacro wrap-object
  [& body]
  `(ObjectWrapper. (do ~@body)))

(defmacro wrap-temporal-accessor
  [keymap & body]
  `(TemporalAccessorWrapper. ~keymap (do ~@body)))

(defmacro wrap-temporal
  [keymap & body]
  `(TemporalWrapper. ~keymap (do ~@body)))

(defn get-field
  ([obj fields keyword]
   (get-field obj fields keyword nil))
  ([obj fields keyword notfound]
   (if-let [field (fields keyword)]
     (if (.isSupported obj field)
         (.get obj (fields keyword))
         notfound)
     (throw (IllegalArgumentException. (str "Unknown field: " keyword))))))

(defn get-entry
  [obj fields keyword]
  (if-let [field (fields keyword)]
    (if (.isSupported obj field)
        (reify clojure.lang.IMapEntry
          (key [this] keyword)
          (val [this] (.get obj field)))
        nil)
    (throw (IllegalArgumentException. (str "Unknown field: " keyword)))))

(defn assoc-field
  [obj fields keyword value]
  (if-let [field (fields keyword)]
    (.with obj field value)
    (throw (IllegalArgumentException. (str "Unknown field: " keyword)))))

(defmacro defwrapper [name type fields]
  (let [class (resolve type)]
    (cond
      ((supers class) Temporal)
      `(do
         (deftype ~name [parent#]
           java.lang.Object
           (equals [this# obj#] (.equals parent# obj#))
           (hashCode [this#] (.hashCode parent#))
           (toString [this#] (.toString parent#))
           clojure.lang.IDeref
           (deref [this#] parent#)
           clojure.lang.ILookup
           (valAt [this# key#] (get-field parent# ~fields key#))
           (valAt [this# key# notfound#]
             (get-field parent# ~fields key# notfound#))
           clojure.lang.Associative
           (containsKey [this# key#]
             (.isSupported parent# (~fields key#)))
           (entryAt [this# key#]
             (get-entry parent# ~fields key#))
           (assoc [this# key# val#]
             (new ~name (assoc-field parent# ~fields key# val#))))
         (defmethod print-method ~name [o# w#]
           (.write w# (str "#<" (.getSimpleName (class o#)) " " (.toString o#) ">"))))
      ((supers class) TemporalAccessor)
      `(do
         (deftype ~name [parent#]
           java.lang.Object
           (equals [this# obj#] (.equals parent# obj#))
           (hashCode [this#] (.hashCode parent#))
           (toString [this#] (.toString parent#))
           clojure.lang.IDeref
           (deref [this#] parent#)
           clojure.lang.ILookup
           (valAt [this# key#] (get-field parent# ~fields key#))
           (valAt [this# key# notfound#]
             (get-field parent# ~fields key# notfound#)))
         (defmethod print-method ~name [o# w#]
           (.write w# (str "#<" (.getSimpleName (class o#)) " " (.toString o#) ">"))))
      :else
      `(do
         (deftype ~name [parent#]
           java.lang.Object
           (equals [this# obj#] (.equals parent# obj#))
           (hashCode [this#] (.hashCode parent#))
           (toString [this#] (.toString parent#))
           clojure.lang.IDeref
           (deref [this#] parent#))
         (defmethod print-method ~name [o# w#]
           (.write w# (str "#<" (.getSimpleName (class o#)) " " (.toString o#) ">")))))))

