DROP TABLE CLIENT IF EXISTS;
DROP TABLE SALESMAN IF EXISTS;

CREATE TABLE CLIENT (
	id INTEGER PRIMARY KEY,
	name VARCHAR(100),
	address VARCHAR(256),
	hobby VARCHAR(256),
	comments VARCHAR(2048),
	lastMeeting DATE,
	salesman INTEGER
);

CREATE TABLE SALESMAN (
	id INTEGER PRIMARY KEY,
	lastname VARCHAR(100),
	firstname VARCHAR(100),
)

INSERT INTO SALESMAN VALUES
    (1, 'Smith', 'Alan'),
    (2, 'Rowland', 'Steve'),
    (3, 'Mac Lane', 'Jason'),
    (4, 'Brian', 'Kelly')

INSERT INTO CLIENT VALUES
    ( 1, 'Préseau', '22 rue Solferino', 'Banana eater', '', NULL, 1),
    ( 2, 'Carrington', '609 rue du 5ieme régiment', 'Likes Cowboys', '', NULL, 2),
    ( 3, 'Barnowsky', '548 rue Creeptown', 'Not cooking', '', NULL, 3),
    ( 4, 'Brown', '34 rue Californie', 'Nursering', '', NULL, 4),
    ( 5, 'Giordino', '1234 rue Harvard', 'Intelligence', '', NULL, 1),
    ( 6, 'Svetlanova', '16 rue de Minsk', 'Poisons and cooking', '', NULL, 2),
    ( 7, 'Martin', '18 rue Giordino', 'Keyboards', '', NULL, 3),
    ( 8, 'Jones', '374 rue Watts', 'Piloting Helicopters', '', NULL, 4),
    ( 9, 'Mangouste', '12 rue Greenfalls', 'Hunting', '', NULL, 1),
    (10, 'Los Santos', '56 rue Jacinto', 'Parkour', '', NULL, 2),
    (11, 'Amos', '1927 rue Irgoun', 'Conspirations', '', NULL, 3),
    (12, 'Mullway', '7 rue Verde', 'Jungle', '', NULL, 4),
    (13, 'Sheridan', '22 rue Wally', 'Leadership', '', NULL, 1)

-- Join test
-- SELECT * FROM CLIENT INNER JOIN SALESMAN ON CLIENT.SALESMAN = SALESMAN.ID WHERE SALESMAN.lastname = 'Smith'
