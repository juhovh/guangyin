(ns guangyin.internal.types
  (:import (guangyin.internal.types ObjectWrapper TemporalAccessorWrapper TemporalWrapper)))

(defmethod print-method ObjectWrapper
  [obj writer]
  (print-method (.getWrapped obj) writer))

(defn wrapped-instance?
  [^Class c x]
  (and (instance? ObjectWrapper x)
       (instance? c (.getWrapped x))))

(defmacro wrap-object
  [& body]
  `(ObjectWrapper. (do ~@body)))

(defmacro wrap-temporal-accessor
  [keymap & body]
  `(TemporalAccessorWrapper. ~keymap (do ~@body)))

(defmacro wrap-temporal
  [keymap & body]
  `(TemporalWrapper. ~keymap (do ~@body)))
