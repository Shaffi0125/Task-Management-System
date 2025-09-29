create table users(
	id (primary key auto_increment),
	username varchar(50) not null,
	email varchar(100) not null,
	role varchar(20) not null
);


create table projects(
	id (primary key auto_increment),
	name varchar(50) not null,
	description varchar(100) not null,
	startDate date not null,
	endDate date not null
);


create table tasks(
	id (primary key auto_increment),
	name varchar(50) not null,
	description varchar(100) not null,
	status varchar(20) not null,
	priority int not null,
	dueDate Date not null,
	projectId int not null,
	userId int not null,
	tasks.projectId references users.id,
	tasks.projectId references projects.id
);