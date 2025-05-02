USE [Anilist]
GO
/****** Object:  StoredProcedure [dbo].[UpdateAnimeList]    Script Date: 02/05/2025 12:59:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER                 procedure [dbo].[UpdateAnimeList]
	@title as varchar(500),
	@score as tinyint,
	@userid as int,
	@username as varchar(50)
as

--exec dbo.UpdateAnimeList 'Violet Evergarden', 100

SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED

--declare @dNow datetime = getdate()

if exists (select *
	from AnimeList
	where Title = @title
	 and UserID = @userid)
begin
	update AnimeList
		set Score = @score
		where Title = @title
		 and UserID = @userid
end
else
begin
	insert into AnimeList(Title, Score, UserID, UserName)
	values (@title, @score, @userid, @username)
end
