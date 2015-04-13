(ns guangyin.internal.types
  (:require [guangyin.internal.fields :as fields])
  (:import (guangyin.internal.types ObjectWrapper DurationWrapper
                                    TemporalAmountWrapper
                                    TemporalAccessorWrapper TemporalWrapper)))

(defmethod print-method ObjectWrapper
  [obj writer]
  (print-method @obj writer))

(defprotocol IWrappable
  (wrap [this])
  (unwrap [this]))

(extend-protocol IWrappable
  guangyin.internal.types.ObjectWrapper
  (wrap [this] this)
  (unwrap [this] (deref this))
  java.time.temporal.Temporal
  (wrap [this] (TemporalWrapper. fields/all-iso-fields this))
  (unwrap [this] this)
  java.time.temporal.TemporalAccessor
  (wrap [this] (TemporalAccessorWrapper. fields/all-iso-fields this))
  (unwrap [this] this)
  java.time.Duration
  (wrap [this] (DurationWrapper. fields/all-iso-units this))
  (unwrap [this] this)
  java.time.temporal.TemporalAmount
  (wrap [this] (TemporalAmountWrapper. fields/all-iso-units this))
  (unwrap [this] this)
  java.lang.Object
  (wrap [this] (ObjectWrapper. this))
  (unwrap [this] this)
  nil
  (wrap [this] nil)
  (unwrap [this] nil))
