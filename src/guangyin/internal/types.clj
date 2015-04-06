(ns guangyin.internal.types
  (:import (java.time.temporal TemporalAccessor Temporal)))

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
        `(deftype ~name [parent#]
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
      ((supers class) TemporalAccessor)
        `(deftype ~name [parent#]
           clojure.lang.IDeref
           (deref [this#] parent#)
           clojure.lang.ILookup
           (valAt [this# key#] (get-field parent# ~fields key#))
           (valAt [this# key# notfound#]
             (get-field parent# ~fields key# notfound#)))
      :else
        `(deftype ~name [parent#]
           clojure.lang.IDeref
           (deref [this#] parent#)))))
