
Sparkngin JSON Module
---------------------

**How to use this module ?**
Nginx Server source needs to be download inorder to build this module.
So download nginx-1.*.* from nginx website and then follow below instructions.

*Note: echo module is helpful to test this module so please download this also*

```
$ git clone https://github.com/mehikmat/Sparkngin.git
$ cd ../nginx-1.4.4 
$ ./configure --add-module=/vagrant/echo-nginx-module --add-module=/vagrant/Sparkngin/json_module
$ make 
$ sudo make install
$ sudo cp /vagrant/nginx.conf  /usr/local/nginx/conf/nginx.conf 
$ sudo /usr/local/nginx/sbin/nginx -s stop (if already stared, stop it first)
$ sudo /usr/local/nginx/sbin/nginx
```
