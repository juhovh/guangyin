(defproject guangyin/guangyin "0.2.0"
  :description "Clojure date and time library wrapping java.time"
  :url "https://github.com/juhovh/guangyin"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :javac-options ["-target" "1.8" "-source" "1.8"]
  :profiles {:dev {:plugins [[codox "0.8.11"]
                             [lein-cloverage "1.0.2"]]
                   :codox {:include [guangyin.core guangyin.format]}}})
