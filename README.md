# Secret Hitler

## Setup

* Cria um projeto no Eclipse;
* Adiciona a biblioteca _jade.jar_ ao projeto.
* Corre o programa com a classe _jade.Boot_, usando o argumento _-gui_


## Cenário

Cada jogador é aleatoriamente nomeado para ser um liberal ou um fascista e um jogador será o Hitler. Os fascistas, que se conhecem uns aos outros, têm de se coordenar para causar a desconfiança entre os outros jogadores e para eleger o Hitler como Chanceler mais tarde; os liberais, ao contrário dos fascistas, não se conhecem(e consequentemente não sabem quem são os fascistas) e precisam de encontrar e impedir o Hitler. A equipa dos liberais é sempre composta pela maioria dos jogadores. O Hitler, tal como os liberais, não sabe quem é quem.

Em cada ronda, o Presidente selecionado(vai rodando para o jogador seguinte) escolhe um Chanceler e, após serem aprovados por todos os jogadores, terão de eleger uma lei. São retiradas três cartas do baralho e o Presidente descarta uma, passando as restantes para o Chanceler, que faz a escolha final. 

O objetivo dos liberais é fazer passar cinco leis liberais ou assassinarem o Hitler. O objetivo dos fascistas é passar seis politicas fascistas ou eleger o Hitler como Chanceler após três leis fascistas terem sido aprovadas.

Cada vez que a eleição do Presidente e Chanceler for recusada, o Rastreador de Eleições avança uma vez. Se avançar três vezes seguidas, a carta do topo do baralho será a nova lei. O Rastreador volta para o início caso seja aprovada uma lei.

![Board](https://raw.githubusercontent.com/LastLombax/AIAD-FEUP/master/Board.PNG?token=AYlAMevu4sBBhoDwOUyy7u4RC0QqaoSWks5b9RvawA%3D%3D "Tabuleiro")

### Regras fascistas

Existem algumas regras após um certo número de leis fascistas aprovadas

* Se 2 leis aprovadas - O Presidente da ronda investiga a associação de um jogador;
* Se 3 leis aprovadas - O Presidente da ronda escolhe o próximo Presidente. A partir desta pontuação para a equipa fascista, se o Hitler for votado como Chanceler, os fascistas ganham;
* Se 4 leis aprovadas - O Presidente da ronda elimina um jogador;
* Se 5 leis aprovadas - O Presidente da ronda elimina um jogador e o poder _Veto_ é desbloqueado;


### Eliminar um jogador

O Presidente executa um jogador selecionando-o. Se esse jogador for o Hitler, o jogo termina numa vitória liberal. Se o jogador executado não for Hitler, não se saberá se foi um fascista ou foi um liberal morto.


### Veto

O Poder de Veto é uma regra especial permanente que entra em vigor depois de cinco Leis Fascistas terem sido elegidas. O governo ganha uma nova capacidade permanente de descartar todas as três leis se tanto o Chanceler quanto o Presidente concordarem.

O Presidente retira três leis, descarta uma e passa as duas restantes para o Chanceler, como de costume. O Chanceler seleciona uma, e então tanto o Presidente quanto o Chanceler votam a favor ou não de rejeitar os resultados desta eleição em segredo. Em caso afirmativo, as duas leis são descartadas. Se o Presidente ou o Chanceler não concordar, a Lei será aprovada.

Cada uso do Poder de Veto representa um governo inativo e avança o Rastreador de Eleições uma vez.


## TP1

O objetivo do primeiro trabalho foi o desenvolvimento da lógica de jogo e o estabelecimento de comunicação inter-agente utilizando ACLMessages.

### Agents

Cada jogador será um agente, que guardará informações sobre o conhecimento que tem dos outros jogadores, como possível associação, cartas votadas, etc.

### Sistema Multi Agente

Jade

### Ambiente

O ambiente será simulado tendo em conta o número de agentes ativos

### Interações 

O Agente que tiver o papel de Presidente terá de enviar as cartas que recebeu para o Chanceler.

## TP2

No segundo trabalho, o objetivo é criar um modelo geral de cada agente, correndo dezenas de vezes o jogo. Ao guardar informações no fim de cada jogo, pode-se relacionar decisões feitas com a _membership_ a uma equipa. 

Guarda-se dados sobre Hitler, Liberal e Fascista. Com um numero sucessivo de jogos, o modelo é criado e atualizado e eventualmente cada agente lê esse modelo e guarda.

Em cada jogada, cada jogador guarda um histórico dos outros jogadores(neste caso, para o presidente e para o chanceller). 
* Para um jogador K, o jogador guarda o que foi para o chanceller quando o K foi presidente e o mesmo para quando foi chanceller e guarda no fim do jogo se era fascista ou liberal.
* Fazendo isto em imensas partidas, o modelo vai sendo criado e este é usado nos jogadores em efeitos de eleição para decidir se vota sim ou não
