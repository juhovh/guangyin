(ns guangyin.internal.utils)

;; From midje.clojure.core
(defmacro pred-cond
  "Checks each predicate against the item, returning the corresponding
   result if it finds a match, otherwise returning nil.
   Assumes item to be a value, as it will get evaluated multiple times."
  [item pred result & preds+results]
  (cond (= pred :else ) result
        (not (seq preds+results)) `(if (~pred ~item) ~result nil) ;; last condition, but no :else in the form
        :else `(if (~pred ~item)
                 ~result
                 (pred-cond ~item ~@preds+results))))

(defmacro pred-cond-throw
  [item msg & body]
  `(let [x# (pred-cond ~item ~@body)]
     (when (nil? x#) (throw (IllegalArgumentException. ~msg)))
     x#))
