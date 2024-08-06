let stompClient;
var gameId;
let playerType;
let opponentName = "";
const backendUrl = 'http://54.211.46.191:8080/gameplay';

function connectToSocket(gameId) {
    console.log("connecting to the game");
    let socket = new SockJS(backendUrl); 
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to the frame: " + frame);
        stompClient.subscribe("/topic/game-progress/" + gameId, function (response) {
            let data = JSON.parse(response.body);
            console.log(data);
            if((opponentName === "" || opponentName === null) && data.player2 !== null){
                setOponentName(data);
                console.log("set oponnent name");
                startGame();
                console.log("game started");
            }
            else{
                displayResponse(data);
                changeTurn(data);
                checkGameFinished(data);
            }
        })
    })
}


function create_game() {
    resetOpponentName();
    gameOn=false;
    let login = document.getElementById("login").value;
    playerType = 'X';
    if (login == null || login === '') {
        alert("Please enter login");
    } else {
        $.ajax({
            url: "/game/start", 
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "login": login,
                "symbol": playerType
            }),
            success: function (data) {
               //gameId = data.gameId;
                reset();
                connectToSocket(data.gameId);
                //alert("Game id: " + data.gameId);
                setGameId(data.gameId);
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}


function connectToRandom() {
    resetOpponentName();
    let login = document.getElementById("login").value;
    playerType = 'O';
    if (login == null || login === '') {
        alert("Please enter login");
    } else {
        $.ajax({
            url: "/game/connect/random", 
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "login": login,
                "symbol": playerType
            }),
            success: function (data) {
               // gameId = data.gameId;
                reset();
                setGameId(data.gameId);
                connectToSocket(gameId);
                setOponentName(data);
                startGame();
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}

function connectToSpecificGame() {
    resetOpponentName();
    let login = document.getElementById("login").value;
    playerType = 'O';
    if (login == null || login === '') {
        alert("Please enter login");
    } else {
        gameId = document.getElementById("game_id").value;
        if (gameId == null || gameId === '') {
            alert("Please enter game id");
        }
        $.ajax({
            url: "/game/connect", 
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "player": {
                    "login": login,
                    "playerType": playerType
                },
                "gameId": gameId
            }),
            success: function (data) {
                reset();
                connectToSocket(gameId);
                setOponentName(data);
                startGame();
                setGameId(data.gameId);
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}

function setOponentName(data){
    if(playerType === data.player1.symbol){
        opponentName = data.player2.login;
    } else {
        opponentName = data.player1.login;
    }
    console.log('opponent: ' + opponentName);
    document.getElementById("opponentLogin").textContent=opponentName;
    //alert("You're playing with: " + opponentName);
}

function resetOpponentName(){
    opponentName = "";
    document.getElementById("opponentLogin").textContent=opponentName;
}

function setGameId(id){
    this.gameId = id;
    console.log("game id " + id);
    document.getElementById("gameId").textContent=id;
}

function getGameId(){
    return gameId;
}