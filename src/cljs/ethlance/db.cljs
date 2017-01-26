(ns ethlance.db
  (:require [cljs-web3.core :as web3]
            [cljs-time.core :as t]
            [cljs.spec :as s]
            [ethlance.constants :as constants]
            [ethlance.utils :as u]
            [re-frame.core :refer [dispatch]]
            ))

(s/def ::devnet? boolean?)
(s/def ::web3 (complement nil?))
(s/def ::node-url string?)
(s/def ::provides-web3? boolean?)
(s/def ::contracts-not-found? boolean?)
(s/def ::generate-db-on-deploy? boolean?)
(s/def ::drawer-open? boolean?)
(s/def ::search-freelancers-filter-open? boolean?)
(s/def ::search-jobs-filter-open? boolean?)
(s/def ::handler keyword?)
(s/def ::route-params (s/map-of keyword? (u/one-of number? string?)))
(s/def :window/width-size int?)
(s/def ::active-page (s/keys :req-un [::handler] :opt-un [::route-params]))
(s/def ::selected-currency (partial contains? (set (keys constants/currencies))))
(s/def ::open? boolean?)
(s/def ::message string?)
(s/def ::on-request-close fn?)
(s/def ::auto-hide-duration int?)
(s/def ::snackbar (s/keys :req-un [::open? ::message ::on-request-close ::auto-hide-duration]))
(s/def :eth/config (s/map-of keyword? int?))
(s/def ::name string?)
(s/def ::address string?)
(s/def ::bin string?)
(s/def ::abi array?)
(s/def ::setter? boolean?)
(s/def :eth/contracts (s/map-of keyword? (s/keys :req-un [::name] :opt-un [::setter? ::address ::bin ::abi])))
(s/def ::my-addresses (s/coll-of string?))
(s/def ::active-address string?)
(s/def ::my-users-loaded? boolean?)
(s/def :user/id pos?)
(s/def :address/balance u/big-num?)
(s/def :blockchain/addresses (s/map-of string? (s/keys :opt [:user/id :address/balance])))
(s/def :blockchain/connection-error? boolean?)
(s/def ::conversion-rates (s/map-of keyword? number?))

(s/def :user/address u/address?)
(s/def :user/country u/uint?)
(s/def :user/state u/uint?)
(s/def :user/created-on u/date?)
(s/def :user/employer? boolean?)
(s/def :user/freelancer? boolean?)
(s/def :user/gravatar u/bytes32?)
(s/def :user/languages u/uint-coll?)
(s/def :user/languages-count u/uint?)
(s/def :user/name string?)
(s/def :user/status u/uint8?)
(s/def :user/balance u/big-num?)
(s/def :freelancer/available? boolean?)
(s/def :freelancer/avg-rating u/uint8?)
(s/def :freelancer/categories u/uint-coll?)
(s/def :freelancer/categories-count u/uint?)
(s/def :freelancer/contracts u/uint-coll?)
(s/def :freelancer/contracts-count u/uint?)
(s/def :freelancer/description string?)
(s/def :freelancer/hourly-rate u/big-num?)
(s/def :freelancer/job-title u/string-or-nil?)
(s/def :freelancer/ratings-count u/uint?)
(s/def :freelancer/skills u/uint-coll?)
(s/def :freelancer/skills-count u/uint?)
(s/def :freelancer/total-earned u/big-num?)
(s/def :freelancer/total-invoiced u/big-num?)
(s/def :employer/avg-rating u/uint8?)
(s/def :employer/description string?)
(s/def :employer/jobs u/uint-coll?)
(s/def :employer/jobs-count u/uint?)
(s/def :employer/ratings-count u/uint?)
(s/def :employer/total-paid u/big-num?)
(s/def :employer/total-invoiced u/big-num?)

(s/def :app/user (s/keys :opt [:user/id
                               :user/address
                               :user/country
                               :user/state
                               :user/created-on
                               :user/employer?
                               :user/freelancer?
                               :user/gravatar
                               :user/languages-coll
                               :user/languages-count
                               :user/name
                               :user/status
                               :user/balance
                               :freelancer/available?
                               :freelancer/avg-rating
                               :freelancer/categories
                               :freelancer/categories-count
                               :freelancer/contracts
                               :freelancer/contracts-count
                               :freelancer/description
                               :freelancer/hourly-rate
                               :freelancer/job-title
                               :freelancer/ratings-count
                               :freelancer/skills
                               :freelancer/skills-count
                               :freelancer/total-earned
                               :freelancer/total-invoiced
                               :employer/avg-rating
                               :employer/description
                               :employer/jobs
                               :employer/jobs-count
                               :employer/ratings-count
                               :employer/total-paid
                               :employer/total-invoiced]))
(s/def :app/users (s/map-of pos? :app/user))

(s/def :job/id pos?)
(s/def :job/budget u/big-num?)
(s/def :job/category u/uint8?)
(s/def :job/contracts u/uint-coll?)
(s/def :job/contracts-count u/uint?)
(s/def :job/created-on u/date?)
(s/def :job/description string?)
(s/def :job/employer u/uint?)
(s/def :job/estimated-duration u/uint8?)
(s/def :job/experience-level u/uint8?)
(s/def :job/freelancers-needed u/uint8?)
(s/def :job/hiring-done-on u/date-or-nil?)
(s/def :job/hours-per-week u/uint8?)
(s/def :job/language u/uint?)
(s/def :job/payment-type u/uint8?)
(s/def :job/skills u/uint-coll?)
(s/def :job/skills-count u/uint?)
(s/def :job/status u/uint8?)
(s/def :job/title string?)
(s/def :job/total-paid u/big-num?)

(s/def :app/job (s/keys :opt [:job/id
                              :job/budget
                              :job/category
                              :job/contracts
                              :job/contracts-count
                              :job/created-on
                              :job/description
                              :job/employer
                              :job/estimated-duration
                              :job/experience-level
                              :job/freelancers-needed
                              :job/hiring-done-on
                              :job/hours-per-week
                              :job/language
                              :job/payment-type
                              :job/skills
                              :job/skills-count
                              :job/status
                              :job/title
                              :job/total-paid]))

(s/def :app/jobs (s/map-of pos? :app/job))

(s/def :contract/id pos?)
(s/def :invitation/created-on u/date-or-nil?)
(s/def :invitation/description string?)
(s/def :proposal/created-on u/date-or-nil?)
(s/def :proposal/description string?)
(s/def :proposal/rate u/big-num?)
(s/def :contract/created-on u/date-or-nil?)
(s/def :contract/description string?)
(s/def :contract/done-by-freelancer? boolean?)
(s/def :contract/done-on u/date-or-nil?)
(s/def :contract/freelancer u/uint?)
(s/def :contract/invoices u/uint-coll?)
(s/def :contract/invoices-count u/uint?)
(s/def :contract/job u/uint?)
(s/def :contract/status u/uint8?)
(s/def :contract/total-invoiced u/big-num?)
(s/def :contract/total-paid u/big-num?)
(s/def :contract/employer-feedback string?)
(s/def :contract/employer-feedback-on u/date-or-nil?)
(s/def :contract/employer-feedback-rating u/uint8?)
(s/def :contract/freelancer-feedback string?)
(s/def :contract/freelancer-feedback-on u/date-or-nil?)
(s/def :contract/freelancer-feedback-rating u/uint8?)

(s/def :app/contract (s/keys :opt [:contract/id
                                   :invitation/created-on
                                   :invitation/description
                                   :proposal/created-on
                                   :proposal/description
                                   :proposal/rate
                                   :contract/created-on
                                   :contract/description
                                   :contract/done-by-freelancer?
                                   :contract/done-on
                                   :contract/freelancer
                                   :contract/invoices
                                   :contract/invoices-count
                                   :contract/job
                                   :contract/status
                                   :contract/total-invoiced
                                   :contract/total-paid
                                   :contract/employer-feedback
                                   :contract/employer-feedback-on
                                   :contract/employer-feedback-rating
                                   :contract/freelancer-feedback
                                   :contract/freelancer-feedback-on
                                   :contract/freelancer-feedback-rating]))

(s/def :app/contracts (s/map-of pos? :app/contract))

(s/def :invoice/id pos?)
(s/def :invoice/amount u/big-num?)
(s/def :invoice/cancelled-on u/date-or-nil?)
(s/def :invoice/contract u/uint?)
(s/def :invoice/created-on u/date?)
(s/def :invoice/description string?)
(s/def :invoice/paid-on u/date-or-nil?)
(s/def :invoice/status u/uint8?)
(s/def :invoice/worked-from u/date?)
(s/def :invoice/worked-hours u/uint?)
(s/def :invoice/worked-to u/date?)

(s/def :app/invoice (s/keys :opt [:invoice/id
                                  :invoice/amount
                                  :invoice/cancelled-on
                                  :invoice/contract
                                  :invoice/created-on
                                  :invoice/description
                                  :invoice/paid-on
                                  :invoice/status
                                  :invoice/worked-from
                                  :invoice/worked-hours
                                  :invoice/worked-to]))

(s/def :app/invoices (s/map-of pos? :app/invoice))

(s/def :skill/id pos?)
(s/def :skill/name u/bytes32?)
(s/def :skill/creator u/uint?)
(s/def :skill/created-on u/date?)
(s/def :skill/jobs-count u/uint?)
(s/def :skill/jobs u/uint-coll?)
(s/def :skill/blocked? boolean?)
(s/def :skill/freelancers-count u/uint?)
(s/def :skill/freelancers u/uint-coll?)

(s/def :app/skill (s/keys :opt [:skill/id
                                :skill/name
                                :skill/creator 
                                :skill/created-on
                                :skill/jobs-count
                                :skill/jobs
                                :skill/blocked?
                                :skill/freelancers-count
                                :skill/freelancers]))

(s/def :app/skills (s/map-of pos? :app/skill))
(s/def :app/skill-count int?)
(s/def ::skill-load-limit pos?)

(s/def ::db (s/keys :req-un [::devnet? ::node-url ::web3 ::active-page ::provides-web3? ::contracts-not-found?
                             ::generate-db-on-deploy? ::drawer-open? ::search-freelancers-filter-open?
                             ::search-jobs-filter-open? ::selected-currency ::snackbar ::my-addresses ::active-address
                             ::my-users-loaded? ::conversion-rates ::skill-load-limit]
                    :req [:window/width-size :eth/config :eth/contracts :blockchain/addresses
                          :blockchain/connection-error? :app/users :app/jobs :app/contracts :app/invoices
                          :app/skill-count]))

(def default-db
  {:devnet? true
   :web3 nil
   :node-url "http://localhost:8545" #_"http://192.168.0.16:8545/"
   :active-page (u/match-current-location)
   :provides-web3? false
   :contracts-not-found? false
   :generate-db-on-deploy? false
   :window/width-size (u/get-window-width-size js/window.innerWidth)
   :drawer-open? false
   :search-freelancers-filter-open? false
   :search-jobs-filter-open? false
   :selected-currency :eth
   :snackbar {:open? false
              :message ""
              :auto-hide-duration 5000
              :on-request-close #(dispatch [:snackbar/close])}
   :eth/config {:max-user-languages 10
                :min-user-languages 1
                :max-freelancer-categories (dec (count constants/categories))
                :min-freelancer-categories 1
                :max-freelancer-skills 10
                :min-freelancer-skills 1
                :max-job-skills 7
                :min-job-skills 1
                :max-user-description 1000
                :max-job-description 1000
                :min-job-description 100
                :max-invoice-description 500
                :max-feedback 1000
                :min-feedback 50
                :max-job-title 100
                :min-job-title 10
                :max-user-name 40
                :min-user-name 5
                :max-freelancer-job-title 50
                :min-freelancer-job-title 4
                :max-contract-desc 500
                :max-proposal-desc 500
                :max-invitation-desc 500
                :max-skills-create-at-once 50 #_10
                :adding-skills-enabled? 1}
   :eth/contracts {:ethlance-user {:name "EthlanceUser" :setter? true #_#_:address "0xb0f1102af4f36290ec7db1461ab23d5a55460715"}
                   :ethlance-job {:name "EthlanceJob" :setter? true #_#_:address "0x2128629f1546072a0a833041fe4445584d792792"}
                   :ethlance-contract {:name "EthlanceContract" :setter? true #_#_:address "0xa5d81ebae0dfe33a20a52b6cc76cebea6530e2c5"}
                   :ethlance-invoice {:name "EthlanceInvoice" :setter? true #_#_:address "0x348585f2c1f08abd701846df04ef737aaa2979d5"}
                   :ethlance-config {:name "EthlanceConfig" :setter? true #_#_:address "0x410f475553f7e1f701d503fc24fc48822fd1ccb4"}
                   :ethlance-db {:name "EthlanceDB" #_#_:address "0xf3e6364666138d997caf832a7bb0688316ac1e5f"}
                   :ethlance-views {:name "EthlanceViews" #_#_:address "0xa2faa7a777d3efc20453dc073b8d8009db6594a6"}
                   :ethlance-search {:name "EthlanceSearch" #_#_:address "0xac9a6b36d5cbc64238dd34b390e3073c7f30cbeb"}}
   :my-addresses []
   :active-address nil
   :active-user-events nil
   :my-users-loaded? false
   :blockchain/addresses {}
   :blockchain/connection-error? false
   :conversion-rates {}
   :app/users {}
   :app/jobs {}
   :app/contracts {}
   :app/invoices {}
   :app/skills {}
   :app/skill-count 0
   :skill-load-limit 5

   :list/my-users {:items [] :loading? true :params {}}
   :list/contract-invoices {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/job-proposals {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :asc}
   :list/job-feedbacks {:items [] :loading? true :params {} :offset 0 :initial-limit 1 :limit 1 :show-more-limit 2 :sort-dir :desc}
   :list/job-invoices {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/employer-invoices-pending {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/employer-invoices-paid {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/freelancer-invoices-pending {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/freelancer-invoices-paid {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/search-freelancers {:items [] :loading? true :params {} :offset 0 :limit 3}
   :list/search-jobs {:items [] :loading? true :params {} :offset 0 :limit 10}
   :list/freelancer-feedbacks {:items [] :loading? true :params {} :offset 0 :initial-limit 1 :limit 1 :show-more-limit 2 :sort-dir :desc}
   :list/employer-feedbacks {:items [] :loading? true :params {} :offset 0 :initial-limit 1 :limit 1 :show-more-limit 2 :sort-dir :desc}
   :list/freelancer-invitations {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/freelancer-proposals {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/freelancer-contracts {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/freelancer-contracts-open {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/freelancer-contracts-done {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/employer-jobs-open {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/employer-jobs-done {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/employer-jobs {:items [] :loading? true :params {} :offset 0 :limit 4 :sort-dir :desc}
   :list/freelancer-my-open-contracts {:items [] :loading? true :params {}}
   :list/employer-jobs-open-select-field {:items [] :loading? false :params {}}

   :form.invoice/pay-invoice {:loading? false :gas-limit 200000}
   :form.invoice/cancel-invoice {:loading? false :gas-limit 200000}
   :form.job/set-hiring-done {:loading? false :gas-limit 200000}
   :form.job/add-job {:loading? false
                      :gas-limit 2000000
                      :data {:job/title ""
                             :job/description ""
                             :job/skills []
                             :job/language 40
                             :job/budget 0
                             :job/category 0
                             :job/payment-type 1
                             :job/experience-level 1
                             :job/estimated-duration 1
                             :job/hours-per-week 1
                             :job/freelancers-needed 1}
                      :errors #{:job/title :job/description :job/skills :job/category}}
   :form.contract/add-invitation {:loading? false
                                  :gas-limit 700000
                                  :data {:invitation/description ""
                                         :contract/job 0}
                                  :errors #{:contract/job}}

   :form.contract/add-proposal {:loading? false
                                :gas-limit 700000
                                :data {:proposal/description ""
                                       :proposal/rate 0}
                                :errors #{:proposal/description}}

   :form.contract/add-contract {:loading? false
                                :gas-limit 700000
                                :data {:contract/description ""
                                       :contract/hiring-done? false}
                                :errors #{}}

   :form.contract/add-feedback {:loading? false
                                :gas-limit 700000
                                :data {:contract/feedback ""
                                       :contract/feedback-rating 0}
                                :errors #{:contract/feedback}}

   :form.invoice/add-invoice {:loading? false
                              :gas-limit 700000
                              :data {:invoice/contract nil
                                     :invoice/description ""
                                     :invoice/amount 0
                                     :invoice/worked-hours 0
                                     :invoice/worked-from (u/timestamp-js->sol (u/get-time (u/week-ago)))
                                     :invoice/worked-to (u/timestamp-js->sol (u/get-time (t/today-at-midnight)))}
                              :errors #{:invoice/contract}}

   :form.config/add-skills {:loading? false
                            :gas-limit 4500000
                            :data {:skill/names []}
                            :errors #{:skill/names}}

   :form.user/set-user {:loading? false
                        :gas-limit 500000
                        :data {}
                        :errors #{}}

   :form.user/set-freelancer {:loading? false
                              :gas-limit 1000000
                              :data {}
                              :errors #{}
                              :open? false}

   :form.user/set-employer {:loading? false
                            :gas-limit 1000000
                            :data {}
                            :errors #{}
                            :open? false}

   :form.user/register-freelancer {:loading? false
                                   :gas-limit 2000000
                                   :open? true
                                   :data {:user/name ""
                                          :user/email ""
                                          :user/gravatar ""
                                          :user/country 0
                                          :user/languages [40]
                                          :freelancer/available? true
                                          :freelancer/job-title ""
                                          :freelancer/hourly-rate 1
                                          :freelancer/categories []
                                          :freelancer/skills []
                                          :freelancer/description ""}
                                   :errors #{:user/name :user/country :freelancer/job-title
                                             :freelancer/categories :freelancer/skills}}

   :form.user/register-employer {:loading? false
                                 :gas-limit 2000000
                                 :open? true
                                 :data {:user/name ""
                                        :user/email ""
                                        :user/gravatar ""
                                        :user/country 0
                                        :user/languages [40]
                                        :employer/description ""}
                                 :errors #{:user/name :user/country}}


   :form/search-jobs {:search/category 0
                      :search/skills []
                      :search/payment-types [1 2]
                      :search/experience-levels [1 2 3]
                      :search/estimated-durations [1 2 3 4]
                      :search/hours-per-weeks [1 2]
                      :search/min-budget 0
                      :search/min-employer-avg-rating 0
                      :search/min-employer-ratings-count 0
                      :search/country 0
                      :search/state 0
                      :search/language 0
                      :search/offset 0
                      :search/limit 10}

   :form/search-freelancers {:search/category 0
                             :search/skills []
                             :search/min-avg-rating 0
                             :search/min-freelancer-ratings-count 0
                             :search/min-hourly-rate 0
                             :search/max-hourly-rate 0
                             :search/country 0
                             :search/state 0
                             :search/language 0
                             :search/offset 0
                             :search/limit 3}
   }
  )
