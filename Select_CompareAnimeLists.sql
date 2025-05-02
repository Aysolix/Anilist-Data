USE [Anilist]
GO
/****** Object:  StoredProcedure [dbo].[Select_CompareAnimeLists]    Script Date: 02/05/2025 12:59:17 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER                   procedure [dbo].[Select_CompareAnimeLists]
	@user1 as varchar(50),
	@user2 as varchar(50),
	@user3 as varchar(50)
as

--exec dbo.Select_CompareAnimeLists 'AlphaXylon', 'Killuke', 'Shoq'

SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED

Create table #CommonAnimeList (
	Title varchar(200),
	Score int,
	UserName varchar(50)
	)

insert into #CommonAnimeList
	select Title, Score, UserName
		from AnimeList
		where Title in (
			select Title
				from AnimeList
				group by Title
					having count(*) >= 2 -- Change back to = 3
			)
			and UserName in (@user1, @user2, @user3)

-- Insert: Where not exists username for title in #commonanimelist, insert row with null score

-- outer join on temp tables of A+K, K+S, A+S

Create table #ComparisonList (
	Title varchar(200),
	Score1 int,
	Score2 int,
	Score3 int
	)

insert into #ComparisonList
select A.Title, A.Score, K.Score, S.Score
	from #CommonAnimeList as A, #CommonAnimeList as K, #CommonAnimeList as S
	where A.Title = K.Title
		and K.Title = S.Title
		and A.UserName = @user1
		and K.UserName = @user2
		and S.UserName = @user3


update #ComparisonList
	set Score1 = Score1 - 87,
		Score2 = Score2 - 72,
		Score3 = Score3 - 68

select * from #ComparisonList

select Title, (Score1 + Score2 + Score3) as Total
from #ComparisonList
order by Total desc

--select *
--	from #CommonAnimeList as t1
--	left join #ComparisonList as t2
--		on t1.Title = t2.Title
--		where t1.Title in (
--			select Title
--				from AnimeList
--				group by Title
--					having count(*) >= 2
--			)

drop table #CommonAnimeList
drop table #ComparisonList
