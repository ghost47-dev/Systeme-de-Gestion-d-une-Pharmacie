# Systeme-de-Gestion-d-une-Pharmacie

## Todo (anyone is welcome to add/remove items from this list, this is all speculative):
  - [x] Make mvp without any graphics or database integration (__make sure to use hashmaps for stuff that'll eventually become persistent!__).
    - [x] Figure out what the project structure will look like.
    - [x] The user shouldn't have to specify the id when creating anything (the whateverManager should figure out the id when adding whatever).
    - [x] Track risky quantity products.
    - [x] Add ability to view sale history.
    - [x] Implement all the damn commands.
      - [x] The admin stuff that is still undone:
        - [x] view stocks
        - [x] view revenue (chiffre d'affaire)
        - [x] view suppliers perfermance
    - [x] exception to handle notFound error
    - [x] Exceptions on quantity related stuff in product and handling them.
  - [x] database:
  - **Note:** **Database files will be stored in the database directory in the project**
  - **Note:** **All SQL commands of creating the db should be saved we will need them later**
    - [x] design the schema
    - [x] build the models (Produit, Client, ...)
  - [x] Integrate the database with received insight from previous step.
    - [x] Implement it into the code for persistent data [Example video](https://www.youtube.com/watch?v=dQw4w9WgXcQ).
  - [ ] The graphics (hopefully won't be hard, we need to finish other stuff asap to make sure we have ample time for this).
    - [ ] We need more steps here.
  - [ ] profit.

I have an idea. what if, let's say ProductManager, had a boolean called "upToDate"
which is true whenever it is in sync with the db, so when we fetch the products
to view them, it first checks if it is up to date so it can omit the unnecessary
db query.
I think this is a good idea bc it will introduce a lot of new out-of-sync bugs
that'll be very hard to debug.

## BUGS:
  * ShipmentManager line 183 this function is missing a lot of the implementation (wtf it just has comments)
  * ShipmentManager line 170 what if the supplier already exists, this shit will fail miserably.
  * Lots of stuff all around the place is unnecessarily public.
  * What if some client buys a product and then we delete it?
