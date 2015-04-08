(ns guangyin.internal.types
  (:import (guangyin.internal.types ObjectWrapper TemporalAccessorWrapper TemporalWrapper)))

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
