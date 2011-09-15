(setq inferior-lisp-program "lein repl")

(add-hook 'clojure-mode-hook '(lambda () (slime-mode -1)))