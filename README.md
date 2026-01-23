# Systeme-de-Gestion-d-une-Pharmacie

## Todo (anyone is welcome to add/remove items from this list, this is all speculative):
  - [x] A lot of stuff.
  - [x] Fully seperate AddProduct and EditProduct (right now they are semi-seperated, ie the addproductcontroller and editproductcontroller have insane amounts of unused code).
  - [x] while(1) Fix(bugs);
  - [x] profit.

I have an idea. what if, let's say ProductManager, had a boolean called "upToDate"
which is true whenever it is in sync with the db, so when we fetch the products
to view them, it first checks if it is up to date so it can omit the unnecessary
db query.
I think this is a good idea bc it will introduce a lot of new out-of-sync bugs
that'll be very hard to debug.


## BUGS:
  * ShipmentManager line 183 this function is missing a lot of the implementation (wtf it just has comments)
  * ShipmentManager line 170 what if the supplier already exists, this shit will fail miserably.
  * What if some client buys a product and then we delete it?

## NOTES:
  * Lots of stuff all around the place is unnecessarily public.
  * Are we really gonna leave some regex all over the place... really?...
  * mafhimtich.
