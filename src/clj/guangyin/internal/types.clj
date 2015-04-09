(ns guangyin.internal.types
  (:import (guangyin.internal.types ObjectWrapper TemporalAccessorWrapper TemporalWrapper)))

(defmethod print-method ObjectWrapper
  [obj writer]
  (print-method (.getWrapped obj) writer))

(defmacro wrap-object
  [& body]
  `(ObjectWrapper. (do ~@body)))

(defmacro wrap-temporal-accessor
  [keymap & body]
  `(TemporalAccessorWrapper. ~keymap (do ~@body)))

(defmacro wrap-temporal
  [keymap & body]
  `(TemporalWrapper. ~keymap (do ~@body)))
