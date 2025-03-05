CREATE TABLE Recipe
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    vegetarian  BOOLEAN,
    servings    INT,
    ingredients VARCHAR(255) NOT NULL,
    instructions VARCHAR(255) NOT NULL
);

insert into Recipe (name, vegetarian, servings, ingredients, instructions) values ('Pasta', true, 4, 'pasta, tomato sauce, cheese', 'Cook pasta, add tomato sauce, add cheese');
insert into Recipe (name, vegetarian, servings, ingredients, instructions) values ('Salad', true, 2, 'lettuce, tomato, cucumber, dressing', 'Mix lettuce, tomato, cucumber, add dressing');
insert into Recipe (name, vegetarian, servings, ingredients, instructions) values ('Steak', false, 1, 'steak, salt, pepper', 'Season steak with salt and pepper, cook steak');
insert into Recipe (name, vegetarian, servings, ingredients, instructions) values ('Soup', true, 6, 'vegetables, broth', 'Cook vegetables in broth');
insert into Recipe (name, vegetarian, servings, ingredients, instructions) values ('Sandwich', true, 1, 'bread, meat, cheese, lettuce, tomato', 'Assemble sandwich');