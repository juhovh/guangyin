(ns guangyin.internal.types
  (:import (guangyin.internal.types ObjectWrapper TemporalAmountWrapper
                                    TemporalAccessorWrapper TemporalWrapper)))

(defmethod print-method ObjectWrapper
  [obj writer]
  (print-method @obj writer))

(defn wrapped-instance?
  [^Class c x]
  (or (instance? c x) ; FIXME This should be removed
      (and (instance? ObjectWrapper x)
           (instance? c @x))))

(defmacro wrap-object
  [& body]
  `(ObjectWrapper. (do ~@body)))

(defmacro wrap-temporal-accessor
  [keymap & body]
  `(TemporalAccessorWrapper. ~keymap (do ~@body)))

(defmacro wrap-temporal
  [keymap & body]
  `(TemporalWrapper. ~keymap (do ~@body)))

(defmacro wrap-temporal-amount
  [keymap & body]
  `(TemporalAmountWrapper. ~keymap (do ~@body)))
