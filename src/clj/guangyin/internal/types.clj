(ns guangyin.internal.types
  (:require [guangyin.internal.fields :as fields])
  (:import (guangyin.internal.types IWrapper ObjectWrapper DurationWrapper
                                    TemporalAmountWrapper
                                    TemporalAccessorWrapper TemporalWrapper
                                    DateTimeFormatterWrapper)))

(defmethod print-method IWrapper
  [obj writer]
  (print-method @obj writer))

(prefer-method print-method IWrapper clojure.lang.IDeref)
(prefer-method print-method IWrapper clojure.lang.IPersistentMap)

(defprotocol PWrappable
  (wrap [this])
  (unwrap [this]))

(extend-protocol PWrappable
  guangyin.internal.types.IWrapper
  (wrap [this] this)
  (unwrap [this] (deref this))
  java.time.format.DateTimeFormatter
  (wrap [this] (DateTimeFormatterWrapper. this))
  (unwrap [this] this)
  java.time.temporal.Temporal
  (wrap [this] (TemporalWrapper. fields/all-fields this))
  (unwrap [this] this)
  java.time.temporal.TemporalAccessor
  (wrap [this] (TemporalAccessorWrapper. fields/all-fields this))
  (unwrap [this] this)
  java.time.Duration
  (wrap [this] (DurationWrapper. fields/all-units this))
  (unwrap [this] this)
  java.time.temporal.TemporalAmount
  (wrap [this] (TemporalAmountWrapper. fields/all-units this))
  (unwrap [this] this)
  java.lang.Object
  (wrap [this] (ObjectWrapper. this))
  (unwrap [this] this)
  nil
  (wrap [this] nil)
  (unwrap [this] nil))
